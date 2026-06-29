ARG UBUNTU_VERSION=24.04
ARG CUDA_IMAGE=nvidia/cuda:12.9.1-cudnn-runtime-ubuntu24.04
ARG TORCH_VERSION=2.8.0
ARG QWEN_ASR_VERSION=0.0.6
ARG MODEL_NAME=Qwen3-ASR-0.6B
ARG EMBED_MODEL=false

# Java 构建阶段独立于运行镜像，最终镜像只保留可执行 JAR。
FROM --platform=$BUILDPLATFORM ubuntu:${UBUNTU_VERSION} AS builder

WORKDIR /workspace
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        openjdk-21-jdk-headless \
        maven \
    && rm -rf /var/lib/apt/lists/*
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -Pexamples \
        -pl scaffold-test/scaffold-test-qwen3-asr \
        -am \
        -DskipTests \
        package \
    && cp scaffold-test/scaffold-test-qwen3-asr/target/scaffold-test-qwen3-asr-*.jar /tmp/app.jar

# 默认是空模型上下文。内置模型时使用
# --build-context model-context=/path/to/Qwen3-ASR-0.6B 覆盖它。
FROM scratch AS model-context

FROM --platform=$BUILDPLATFORM ubuntu:${UBUNTU_VERSION} AS model-bundle
ARG EMBED_MODEL
ARG MODEL_NAME
RUN mkdir -p /models
RUN --mount=type=bind,from=model-context,source=/,target=/model-source,ro \
    set -eux; \
    case "$EMBED_MODEL" in true|false) ;; *) echo "EMBED_MODEL must be true or false" >&2; exit 2 ;; esac; \
    if [ "$EMBED_MODEL" = "false" ]; then \
        echo "Building image without embedded model"; \
        exit 0; \
    fi; \
    echo "$MODEL_NAME" | grep -Eq '^[A-Za-z0-9._-]+$'; \
    test -f /model-source/config.json; \
    find /model-source -maxdepth 1 -type f -name 'model*.safetensors' -size +1M -print -quit | grep -q .; \
    target_model="/models/$MODEL_NAME"; \
    mkdir -p "$target_model"; \
    cp -a /model-source/. "$target_model/"; \
    echo "Embedded model from model-context into $target_model"

# CPU 和 GPU 分别安装对应的 PyTorch wheel，避免运行镜像携带无用后端。
FROM ubuntu:${UBUNTU_VERSION} AS cpu-runtime
ARG TORCH_VERSION
ARG QWEN_ASR_VERSION
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        ffmpeg \
        libsndfile1 \
        libsox-fmt-all \
        openjdk-21-jre-headless \
        python3 \
        python3-pip \
        python3-venv \
        sox \
    && rm -rf /var/lib/apt/lists/* \
    && python3 -m venv /opt/qwen-asr \
    && /opt/qwen-asr/bin/pip install --no-cache-dir --upgrade pip \
    && /opt/qwen-asr/bin/pip install --no-cache-dir \
        "torch==${TORCH_VERSION}+cpu" \
        --index-url https://download.pytorch.org/whl/cpu \
    && /opt/qwen-asr/bin/pip install --no-cache-dir "qwen-asr==${QWEN_ASR_VERSION}"
FROM ${CUDA_IMAGE} AS gpu-runtime
ARG TORCH_VERSION
ARG QWEN_ASR_VERSION
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        ffmpeg \
        libsndfile1 \
        libsox-fmt-all \
        openjdk-21-jre-headless \
        python3 \
        python3-pip \
        python3-venv \
        sox \
    && rm -rf /var/lib/apt/lists/* \
    && python3 -m venv /opt/qwen-asr \
    && /opt/qwen-asr/bin/pip install --no-cache-dir --upgrade pip \
    && /opt/qwen-asr/bin/pip install --no-cache-dir \
        "torch==${TORCH_VERSION}+cu129" \
        --index-url https://download.pytorch.org/whl/cu129 \
    && /opt/qwen-asr/bin/pip install --no-cache-dir "qwen-asr==${QWEN_ASR_VERSION}"
# CPU 服务默认使用 float32，以覆盖不支持半精度的通用主机。
FROM cpu-runtime AS cpu
ARG MODEL_NAME
ARG EMBED_MODEL
COPY --from=builder /tmp/app.jar /app/app.jar
COPY --from=model-bundle /models/ /models/
COPY scaffold-test/scaffold-test-qwen3-asr/scripts/qwen3_asr_cli.py /app/scripts/qwen3_asr_cli.py
LABEL com.scaffold.qwen3-asr.model.embedded="${EMBED_MODEL}" \
      com.scaffold.qwen3-asr.model.name="${MODEL_NAME}"
ENV QWEN3_ASR_PYTHON=/opt/qwen-asr/bin/python \
    QWEN3_ASR_SCRIPT=/app/scripts/qwen3_asr_cli.py \
    QWEN3_ASR_MODEL_PATH=/models/${MODEL_NAME} \
    QWEN3_ASR_SERVER_ADDRESS=0.0.0.0 \
    QWEN3_ASR_SERVER_PORT=8093 \
    QWEN3_ASR_UPLOAD_DIR=/tmp/qwen3-asr-uploads \
    QWEN3_ASR_DEVICE_MAP=cpu \
    QWEN3_ASR_DTYPE=float32 \
    HF_HUB_OFFLINE=1 \
    TRANSFORMERS_OFFLINE=1 \
    HF_DATASETS_OFFLINE=1
RUN useradd --create-home --uid 10001 qwen
USER 10001:10001
WORKDIR /app
EXPOSE 8093
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD ["/opt/qwen-asr/bin/python", "-c", "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8093/qwen3-asr/health', timeout=5)"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# GPU 服务固定使用第一张 CUDA 设备和 float16。
FROM gpu-runtime AS gpu
ARG MODEL_NAME
ARG EMBED_MODEL
COPY --from=builder /tmp/app.jar /app/app.jar
COPY --from=model-bundle /models/ /models/
COPY scaffold-test/scaffold-test-qwen3-asr/scripts/qwen3_asr_cli.py /app/scripts/qwen3_asr_cli.py
LABEL com.scaffold.qwen3-asr.model.embedded="${EMBED_MODEL}" \
      com.scaffold.qwen3-asr.model.name="${MODEL_NAME}"
ENV QWEN3_ASR_PYTHON=/opt/qwen-asr/bin/python \
    QWEN3_ASR_SCRIPT=/app/scripts/qwen3_asr_cli.py \
    QWEN3_ASR_MODEL_PATH=/models/${MODEL_NAME} \
    QWEN3_ASR_SERVER_ADDRESS=0.0.0.0 \
    QWEN3_ASR_SERVER_PORT=8093 \
    QWEN3_ASR_UPLOAD_DIR=/tmp/qwen3-asr-uploads \
    QWEN3_ASR_DEVICE_MAP=cuda:0 \
    QWEN3_ASR_DTYPE=float16 \
    HF_HUB_OFFLINE=1 \
    TRANSFORMERS_OFFLINE=1 \
    HF_DATASETS_OFFLINE=1
RUN useradd --create-home --uid 10001 qwen
USER 10001:10001
WORKDIR /app
EXPOSE 8093
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD ["/opt/qwen-asr/bin/python", "-c", "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8093/qwen3-asr/health', timeout=5)"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 未指定构建 target 时生成兼容性更广的 CPU 镜像。
FROM cpu AS default
