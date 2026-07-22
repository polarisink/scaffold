#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="$(cd "${MODULE_DIR}/../.." && pwd)"
TARGET="${1:-host}"

case "${TARGET}" in
  host)
    echo "Building scaffold-test-native executable for the current host..."
    cd "${PROJECT_DIR}"
    ./mvnw -Pnative -pl scaffold-test/scaffold-test-native -am \
      -Dmaven.test.skip=true clean package
    echo "Native executable: ${MODULE_DIR}/target/scaffold-test-native"
    ;;
  linux)
    command -v docker >/dev/null 2>&1 || {
      echo "docker is required to build a Linux native executable." >&2
      exit 1
    }
    docker info >/dev/null 2>&1 || {
      echo "Docker daemon is not available." >&2
      exit 1
    }

    OUTPUT_DIR="${MODULE_DIR}/target/native-linux"
    mkdir -p "${OUTPUT_DIR}"
    echo "Building scaffold-test-native Linux native executable..."
    cd "${PROJECT_DIR}"
    docker build \
      --file scaffold-test/scaffold-test-native/Dockerfile.native \
      --target native-export \
      --output "type=local,dest=${OUTPUT_DIR}" \
      .
    chmod +x "${OUTPUT_DIR}/scaffold-test-native"
    echo "Linux native executable: ${OUTPUT_DIR}/scaffold-test-native"
    ;;
  *)
    echo "Usage: $0 [host|linux]" >&2
    echo "  host   Build for the current operating system (default)." >&2
    echo "  linux  Build a Linux ELF executable with Docker." >&2
    exit 2
    ;;
esac
