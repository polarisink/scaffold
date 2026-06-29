#!/usr/bin/env python3
import argparse
import contextlib
import json
import os
import sys

# Java 端按 UTF-8 解析 JSON Lines；显式设置编码以兼容 Windows 默认代码页。
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(encoding="utf-8")


def parse_args():
    """解析单次 CLI 模式和常驻 Worker 模式共用的启动参数。"""
    parser = argparse.ArgumentParser(description="Offline local Qwen3-ASR inference CLI.")
    parser.add_argument("--worker", action="store_true", help="Keep the model loaded and process JSON requests on stdin.")
    parser.add_argument("--model-path", required=True, help="Local Qwen3-ASR model directory.")
    parser.add_argument("--audio", help="Local audio file path. Required unless --worker is used.")
    parser.add_argument("--language", default=None, help='Optional language, for example "Chinese" or "English".')
    parser.add_argument("--return-time-stamps", action="store_true", help="Return timestamps with Qwen3-ForcedAligner.")
    parser.add_argument("--forced-aligner-path", default=None, help="Local Qwen3-ForcedAligner model directory.")
    parser.add_argument("--device-map", default="auto", help='Transformers device_map: "auto", "cuda:0", "mps" or "cpu".')
    parser.add_argument("--dtype", default="auto", choices=["auto", "bfloat16", "float16", "float32"], help="Torch dtype.")
    parser.add_argument("--max-inference-batch-size", type=int, default=8)
    parser.add_argument("--max-new-tokens", type=int, default=512)
    return parser.parse_args()


def torch_dtype(torch, dtype):
    """将配置中的精度名称转换成 torch.dtype。"""
    if dtype == "auto":
        return torch_dtype(torch, resolve_dtype(torch, "auto"))
    if dtype == "bfloat16":
        return torch.bfloat16
    if dtype == "float16":
        return torch.float16
    return torch.float32


def resolve_device_map(torch, device_map):
    """未指定设备时按 CUDA、Apple MPS、CPU 的顺序选择可用后端。"""
    if device_map != "auto":
        return device_map
    if torch.cuda.is_available():
        return "cuda:0"
    if hasattr(torch.backends, "mps") and torch.backends.mps.is_available():
        return "mps"
    return "cpu"


def resolve_dtype(torch, dtype):
    """根据实际后端选择兼顾兼容性与性能的默认精度。"""
    if dtype != "auto":
        return dtype
    if torch.cuda.is_available():
        return "bfloat16"
    if hasattr(torch.backends, "mps") and torch.backends.mps.is_available():
        return "float16"
    return "float32"


def timestamp_to_dict(item):
    """把 qwen-asr 的时间戳对象转换为 Java DTO 使用的 camelCase 字段。"""
    return {
        "text": getattr(item, "text", None),
        "startTime": getattr(item, "start_time", None),
        "endTime": getattr(item, "end_time", None),
    }


def validate_args(args):
    """在加载大型模型前尽早报告路径和参数错误。"""
    if not os.path.isdir(args.model_path):
        raise SystemExit(f"model path does not exist: {args.model_path}")
    if not args.worker and not args.audio:
        raise SystemExit("--audio is required unless --worker is used")
    if args.audio and not os.path.isfile(args.audio):
        raise SystemExit(f"audio file does not exist: {args.audio}")
    if args.return_time_stamps and not args.forced_aligner_path:
        raise SystemExit("--forced-aligner-path is required when --return-time-stamps is enabled")
    if args.forced_aligner_path and not os.path.isdir(args.forced_aligner_path):
        raise SystemExit(f"forced aligner path does not exist: {args.forced_aligner_path}")


def load_model(args):
    """仅从本地目录加载 ASR 模型及可选的 Forced Aligner。"""
    # 第三方库可能向 stdout 打印进度；重定向后可避免破坏 JSON Lines 协议。
    with contextlib.redirect_stdout(sys.stderr):
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

    with contextlib.redirect_stdout(sys.stderr):
        model = Qwen3ASRModel.from_pretrained(args.model_path, **init_kwargs)
    return model, device_map, dtype


def transcribe(model, args, audio, language, return_time_stamps, device_map, dtype):
    """执行一次转写，并整理成与 Qwen3AsrResult 一致的响应结构。"""
    if not os.path.isfile(audio):
        raise ValueError(f"audio file does not exist: {audio}")
    with contextlib.redirect_stdout(sys.stderr):
        results = model.transcribe(
            audio=audio,
            language=language or None,
            return_time_stamps=return_time_stamps,
        )
    result = results[0]
    time_stamps = None
    if return_time_stamps and getattr(result, "time_stamps", None):
        time_stamps = [timestamp_to_dict(item) for item in result.time_stamps]

    return {
        "language": getattr(result, "language", None),
        "text": getattr(result, "text", None),
        "timeStamps": time_stamps,
        "audioPath": audio,
        "modelPath": args.model_path,
        "deviceMap": device_map,
        "dtype": dtype,
    }


def emit(message):
    """输出一条完整 JSON 消息并立即刷新，供 Java Worker 阻塞读取。"""
    print(json.dumps(message, ensure_ascii=False), flush=True)


def run_worker(model, args, device_map, dtype):
    """复用已加载模型，逐行处理 Java 进程发送的 JSON 请求。"""
    # ready 必须在模型加载完成后发送，Java 端据此判断服务是否可用。
    emit({"type": "ready", "ok": True, "deviceMap": device_map, "dtype": dtype})
    for line in sys.stdin:
        try:
            request = json.loads(line)
            result = transcribe(
                model,
                args,
                request.get("audio", ""),
                request.get("language"),
                bool(request.get("returnTimeStamps", False)),
                device_map,
                dtype,
            )
            emit({"type": "result", "ok": True, "result": result})
        except Exception as exc:
            # 单次请求失败不退出 Worker，后续请求仍可继续使用已加载模型。
            emit({"type": "result", "ok": False, "error": str(exc)})


def main():
    """配置严格离线环境，加载一次模型后进入指定运行模式。"""
    args = parse_args()
    os.environ.setdefault("HF_HUB_OFFLINE", "1")
    os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
    os.environ.setdefault("HF_DATASETS_OFFLINE", "1")
    validate_args(args)
    model, device_map, dtype = load_model(args)
    if args.worker:
        run_worker(model, args, device_map, dtype)
        return
    result = transcribe(model, args, args.audio, args.language, args.return_time_stamps, device_map, dtype)
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(str(exc), file=sys.stderr)
        raise
