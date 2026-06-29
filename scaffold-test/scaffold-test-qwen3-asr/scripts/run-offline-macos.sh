#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <model-dir> <audio-file> [language]" >&2
  exit 2
fi

MODEL_DIR="$1"
AUDIO_FILE="$2"
LANGUAGE="${3:-Chinese}"
# 可通过环境变量选择安装了 qwen-asr 的虚拟环境解释器。
PYTHON_BIN="${QWEN3_ASR_PYTHON:-python3}"

# 禁止依赖库在推理时联网下载模型或数据集。
export HF_HUB_OFFLINE=1
export TRANSFORMERS_OFFLINE=1
export HF_DATASETS_OFFLINE=1
export PYTHONIOENCODING=UTF-8

"$PYTHON_BIN" "$(dirname "$0")/qwen3_asr_cli.py" \
  --model-path "$MODEL_DIR" \
  --audio "$AUDIO_FILE" \
  --language "$LANGUAGE" \
  --device-map auto \
  --dtype auto
