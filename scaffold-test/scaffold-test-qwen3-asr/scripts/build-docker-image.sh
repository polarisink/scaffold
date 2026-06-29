#!/usr/bin/env bash
set -euo pipefail

usage() {
  local exit_code="${1:-2}"
  cat >&2 <<'EOF'
Usage:
  build-docker-image.sh [options]

Options:
  --variant <cpu|gpu>       Runtime variant. Default: cpu
  --arch <amd64|arm64>      Target architecture. Default: current host
  --model-name <name>       Model directory name in the container.
                             Default: Qwen3-ASR-0.6B
  --external-model          Do not embed model weights. This is the default.
  --embed-model <path>      Embed the model directory at the given host path.
  --tag <image:tag>         Output image tag. Default depends on mode.
  -h, --help                Show this help.

Examples:
  build-docker-image.sh --variant cpu --external-model
  build-docker-image.sh --variant cpu --arch arm64 \
    --embed-model ../models/Qwen3-ASR-0.6B \
    --tag scaffold-qwen3-asr:cpu-embedded
EOF
  exit "$exit_code"
}

host_arch() {
  case "$(uname -m)" in
    x86_64) echo amd64 ;;
    arm64|aarch64) echo arm64 ;;
    *) echo "Unsupported host architecture: $(uname -m)" >&2; exit 2 ;;
  esac
}

VARIANT=cpu
ARCH="$(host_arch)"
MODEL_NAME=Qwen3-ASR-0.6B
EMBED_MODEL=false
MODEL_SOURCE=
IMAGE_TAG=

while [[ $# -gt 0 ]]; do
  case "$1" in
    --variant) [[ $# -ge 2 ]] || usage; VARIANT="$2"; shift 2 ;;
    --arch) [[ $# -ge 2 ]] || usage; ARCH="$2"; shift 2 ;;
    --model-name) [[ $# -ge 2 ]] || usage; MODEL_NAME="$2"; shift 2 ;;
    --external-model) EMBED_MODEL=false; MODEL_SOURCE=; shift ;;
    --embed-model) [[ $# -ge 2 ]] || usage; EMBED_MODEL=true; MODEL_SOURCE="$2"; shift 2 ;;
    --tag) [[ $# -ge 2 ]] || usage; IMAGE_TAG="$2"; shift 2 ;;
    -h|--help) usage 0 ;;
    *) echo "Unknown option: $1" >&2; usage ;;
  esac
done

[[ "$VARIANT" == cpu || "$VARIANT" == gpu ]] || usage
[[ "$ARCH" == amd64 || "$ARCH" == arm64 ]] || usage
[[ "$MODEL_NAME" =~ ^[A-Za-z0-9._-]+$ ]] || { echo "Invalid model name: $MODEL_NAME" >&2; exit 2; }
command -v docker >/dev/null 2>&1 || { echo "docker is required" >&2; exit 2; }

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MODULE_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
REPO_ROOT="$(cd "$MODULE_DIR/../.." && pwd)"
MODEL_MODE=external

if [[ "$EMBED_MODEL" == true ]]; then
  MODEL_MODE=embedded
  [[ -n "$MODEL_SOURCE" && -d "$MODEL_SOURCE" ]] || {
    echo "Embedded model directory does not exist: $MODEL_SOURCE" >&2
    exit 2
  }
  MODEL_SOURCE="$(cd "$MODEL_SOURCE" && pwd)"
  [[ -f "$MODEL_SOURCE/config.json" ]] || { echo "Missing model config: $MODEL_SOURCE/config.json" >&2; exit 2; }
  find "$MODEL_SOURCE" -maxdepth 1 -type f -name 'model*.safetensors' -size +1M -print -quit | grep -q . || {
    echo "No complete model*.safetensors found in: $MODEL_SOURCE" >&2
    exit 2
  }
fi

IMAGE_TAG="${IMAGE_TAG:-scaffold-qwen3-asr:${VARIANT}-${MODEL_MODE}}"

echo "Building $IMAGE_TAG"
echo "  platform: linux/$ARCH"
echo "  variant:  $VARIANT"
echo "  model:    $MODEL_NAME ($MODEL_MODE)"

DOCKER_ARGS=(
  buildx build
  --load
  --platform "linux/$ARCH"
  --target "$VARIANT"
  --build-arg "EMBED_MODEL=$EMBED_MODEL"
  --build-arg "MODEL_NAME=$MODEL_NAME"
)
if [[ "$EMBED_MODEL" == true ]]; then
  DOCKER_ARGS+=(--build-context "model-context=$MODEL_SOURCE")
fi
DOCKER_ARGS+=(
  -f "$MODULE_DIR/Dockerfile"
  -t "$IMAGE_TAG"
  "$REPO_ROOT"
)

docker "${DOCKER_ARGS[@]}"

docker image inspect "$IMAGE_TAG" --format \
  'Built {{.RepoTags}} (arch={{.Architecture}}, size={{.Size}}, embedded-model={{index .Config.Labels "com.scaffold.qwen3-asr.model.embedded"}})'
