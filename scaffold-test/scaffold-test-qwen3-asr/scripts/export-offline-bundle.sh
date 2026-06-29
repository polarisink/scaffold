#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 <cpu|gpu> <amd64|arm64> <model-name> <model-path> <output-dir>" >&2
  exit 2
}

[[ $# -eq 5 ]] || usage

VARIANT="$1"
ARCH="$2"
MODEL_NAME="$3"
MODEL_SOURCE="$4"
OUTPUT_DIR="$5"

[[ "$VARIANT" == "cpu" || "$VARIANT" == "gpu" ]] || usage
[[ "$ARCH" == "amd64" || "$ARCH" == "arm64" ]] || usage
[[ "$MODEL_NAME" =~ ^[A-Za-z0-9._-]+$ ]] || { echo "Invalid model name: $MODEL_NAME" >&2; exit 2; }
[[ -d "$MODEL_SOURCE" ]] || { echo "Model directory does not exist: $MODEL_SOURCE" >&2; exit 2; }
MODEL_SOURCE="$(cd "$MODEL_SOURCE" && pwd)"
[[ -f "$MODEL_SOURCE/config.json" ]] || { echo "Missing model config: $MODEL_SOURCE/config.json" >&2; exit 2; }
find "$MODEL_SOURCE" -maxdepth 1 -type f -name 'model*.safetensors' -size +1M -print -quit | grep -q . || {
  echo "No complete model*.safetensors found in: $MODEL_SOURCE" >&2
  exit 2
}
[[ ! -e "$OUTPUT_DIR" ]] || { echo "Output already exists: $OUTPUT_DIR" >&2; exit 2; }

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MODULE_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
IMAGE_TAG="scaffold-qwen3-asr:${MODEL_NAME}-${VARIANT}-${ARCH}"

echo "Building $IMAGE_TAG for linux/$ARCH ..."
docker buildx build \
  --load \
  --platform "linux/$ARCH" \
  --target "$VARIANT" \
  --build-context "model-context=$MODEL_SOURCE" \
  --build-arg "EMBED_MODEL=true" \
  --build-arg "MODEL_NAME=$MODEL_NAME" \
  -f "$MODULE_DIR/Dockerfile" \
  -t "$IMAGE_TAG" \
  "$REPO_ROOT"

mkdir -p "$OUTPUT_DIR"

echo "Exporting self-contained Docker image ..."
docker save "$IMAGE_TAG" | gzip -1 > "$OUTPUT_DIR/image.tar.gz"

cat > "$OUTPUT_DIR/bundle.env" <<EOF
IMAGE_TAG=$IMAGE_TAG
VARIANT=$VARIANT
ARCH=$ARCH
MODEL_NAME=$MODEL_NAME
EMBED_MODEL=true
EOF
cp "$SCRIPT_DIR/import-offline-bundle.sh" "$OUTPUT_DIR/import.sh"
chmod +x "$OUTPUT_DIR/import.sh"

(
  cd "$OUTPUT_DIR"
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum image.tar.gz bundle.env import.sh > SHA256SUMS
  else
    shasum -a 256 image.tar.gz bundle.env import.sh > SHA256SUMS
  fi
)

echo "Offline bundle created: $OUTPUT_DIR"
du -sh "$OUTPUT_DIR"
