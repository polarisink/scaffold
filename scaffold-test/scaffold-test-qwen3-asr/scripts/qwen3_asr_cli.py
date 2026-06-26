#!/usr/bin/env python3
import argparse
import json
import os
import sys

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(encoding="utf-8")


def parse_args():
    parser = argparse.ArgumentParser(description="Offline local Qwen3-ASR inference CLI.")
    parser.add_argument("--model-path", required=True, help="Local Qwen3-ASR model directory.")
    parser.add_argument("--audio", required=True, help="Local audio file path.")
    parser.add_argument("--language", default=None, help='Optional language, for example "Chinese" or "English".')
    parser.add_argument("--return-time-stamps", action="store_true", help="Return timestamps with Qwen3-ForcedAligner.")
    parser.add_argument("--forced-aligner-path", default=None, help="Local Qwen3-ForcedAligner model directory.")
    parser.add_argument("--device-map", default="auto", help='Transformers device_map: "auto", "cuda:0", "mps" or "cpu".')
    parser.add_argument("--dtype", default="auto", choices=["auto", "bfloat16", "float16", "float32"], help="Torch dtype.")
    parser.add_argument("--max-inference-batch-size", type=int, default=8)
    parser.add_argument("--max-new-tokens", type=int, default=512)
    return parser.parse_args()


def torch_dtype(torch, dtype):
    if dtype == "auto":
        return torch_dtype(torch, resolve_dtype(torch, "auto"))
    if dtype == "bfloat16":
        return torch.bfloat16
    if dtype == "float16":
        return torch.float16
    return torch.float32


def resolve_device_map(torch, device_map):
    if device_map != "auto":
        return device_map
    if torch.cuda.is_available():
        return "cuda:0"
    if hasattr(torch.backends, "mps") and torch.backends.mps.is_available():
        return "mps"
    return "cpu"


def resolve_dtype(torch, dtype):
    if dtype != "auto":
        return dtype
    if torch.cuda.is_available():
        return "bfloat16"
    if hasattr(torch.backends, "mps") and torch.backends.mps.is_available():
        return "float16"
    return "float32"


def timestamp_to_dict(item):
    return {
        "text": getattr(item, "text", None),
        "startTime": getattr(item, "start_time", None),
        "endTime": getattr(item, "end_time", None),
    }


def main():
    args = parse_args()
    os.environ.setdefault("HF_HUB_OFFLINE", "1")
    os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
    os.environ.setdefault("HF_DATASETS_OFFLINE", "1")

    if not os.path.isdir(args.model_path):
        raise SystemExit(f"model path does not exist: {args.model_path}")
    if not os.path.isfile(args.audio):
        raise SystemExit(f"audio file does not exist: {args.audio}")
    if args.return_time_stamps and not args.forced_aligner_path:
        raise SystemExit("--forced-aligner-path is required when --return-time-stamps is enabled")
    if args.forced_aligner_path and not os.path.isdir(args.forced_aligner_path):
        raise SystemExit(f"forced aligner path does not exist: {args.forced_aligner_path}")

    import torch
    from qwen_asr import Qwen3ASRModel
    device_map = resolve_device_map(torch, args.device_map)
    dtype = resolve_dtype(torch, args.dtype)

    init_kwargs = {
        "dtype": torch_dtype(torch, dtype),
        "device_map": device_map,
        "max_inference_batch_size": args.max_inference_batch_size,
        "max_new_tokens": args.max_new_tokens,
        "local_files_only": True,
    }
    if args.forced_aligner_path:
        init_kwargs["forced_aligner"] = args.forced_aligner_path
        init_kwargs["forced_aligner_kwargs"] = {
            "dtype": torch_dtype(torch, dtype),
            "device_map": device_map,
            "local_files_only": True,
        }

    model = Qwen3ASRModel.from_pretrained(args.model_path, **init_kwargs)
    results = model.transcribe(
        audio=args.audio,
        language=args.language,
        return_time_stamps=args.return_time_stamps,
    )
    result = results[0]
    time_stamps = None
    if args.return_time_stamps and getattr(result, "time_stamps", None):
        time_stamps = [timestamp_to_dict(item) for item in result.time_stamps]

    print(json.dumps({
        "language": getattr(result, "language", None),
        "text": getattr(result, "text", None),
        "timeStamps": time_stamps,
        "audioPath": args.audio,
        "modelPath": args.model_path,
        "deviceMap": device_map,
        "dtype": dtype,
    }, ensure_ascii=False))


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(str(exc), file=sys.stderr)
        raise
