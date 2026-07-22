#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="$(cd "${MODULE_DIR}/../.." && pwd)"
NATIVE_BINARY="${MODULE_DIR}/target/native-linux/scaffold-test-native"
IMAGE_NAME="${1:-scaffold-test-native:native}"

command -v docker >/dev/null 2>&1 || {
  echo "docker is required to build the image." >&2
  exit 1
}
docker info >/dev/null 2>&1 || {
  echo "Docker daemon is not available." >&2
  exit 1
}

if [[ ! -f "${NATIVE_BINARY}" ]]; then
  echo "Linux native executable not found: ${NATIVE_BINARY}"
  echo "Building it before packaging the Docker image..."
  "${SCRIPT_DIR}/build-native.sh" linux
fi

if [[ ! -f "${NATIVE_BINARY}" ]]; then
  echo "Expected executable was not produced: ${NATIVE_BINARY}" >&2
  exit 1
fi

if command -v file >/dev/null 2>&1 && ! file "${NATIVE_BINARY}" | grep -q 'ELF'; then
  echo "The executable is not a Linux ELF binary:" >&2
  file "${NATIVE_BINARY}" >&2
  echo "Run '${SCRIPT_DIR}/build-native.sh linux' to rebuild it." >&2
  exit 1
fi

chmod +x "${NATIVE_BINARY}"
echo "Packaging ${NATIVE_BINARY} as ${IMAGE_NAME}..."
cd "${PROJECT_DIR}"
docker build \
  --file scaffold-test/scaffold-test-native/Dockerfile.native-binary \
  --tag "${IMAGE_NAME}" \
  scaffold-test/scaffold-test-native

echo "Docker native image: ${IMAGE_NAME}"
