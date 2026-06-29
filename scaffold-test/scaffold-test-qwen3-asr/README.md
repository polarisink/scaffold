# scaffold-test-qwen3-asr

Qwen3-ASR 离线本地语音识别 HTTP 服务。Spring Boot 默认仅监听 `127.0.0.1:8093`，首次转写时启动 Python worker 并加载模型，后续请求复用同一个模型进程。

## HTTP 接口

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `GET` | `/qwen3-asr/health` | 服务及模型加载状态 |
| `POST` | `/qwen3-asr/transcribe` | 通过 `multipart/form-data` 上传音频 |
| `POST` | `/qwen3-asr/transcribe-path` | 转写服务所在机器上的音频文件 |

服务启动后可访问 Knife4j 接口文档 `http://127.0.0.1:8093/doc.html`，直接选择音频文件并执行转写请求。OpenAPI JSON 地址为 `http://127.0.0.1:8093/v3/api-docs`。

### 支持的音频格式

当前已验证以下格式可通过上传接口和本地路径接口进行转写：

- `WAV`
- `MP3`
- `FLAC`
- `OGG`
- `M4A/AAC`
- `WebM/Opus`

音频由 Qwen3-ASR 内部的 `librosa`、`soundfile` 及 Docker 镜像中的 FFmpeg 解码。其他格式可能也能被解码，但不属于当前验证和保证范围。单个上传文件最大为 `512MB`。

本地推理请求会串行执行，避免并发推理抢占显存。上传的临时音频会在请求结束后自动删除。

## 环境选择

默认配置为 `device-map=auto` 和 `dtype=auto`：

- Win10 + NVIDIA：自动使用 `cuda:0` 和 `bfloat16`
- macOS M4：自动使用 `mps` 和 `float16`
- 无可用 GPU：自动使用 `cpu` 和 `float32`

本模块不调用云 API。Java 服务会启动本机 Python 进程，Python 脚本会设置 `HF_HUB_OFFLINE=1`、`TRANSFORMERS_OFFLINE=1`、`HF_DATASETS_OFFLINE=1`，所以模型必须提前下载到本地目录。

## 准备本地模型

先在有网络的机器下载模型，再拷贝到离线机器。常用模型：

- `Qwen/Qwen3-ASR-0.6B`
- `Qwen/Qwen3-ASR-1.7B`
- `Qwen/Qwen3-ForcedAligner-0.6B`，仅时间戳需要

示例：

```bash
huggingface-cli download Qwen/Qwen3-ASR-0.6B --local-dir /data/models/Qwen3-ASR-0.6B
huggingface-cli download Qwen/Qwen3-ForcedAligner-0.6B --local-dir /data/models/Qwen3-ForcedAligner-0.6B
```

## 离线 Python 环境

在联网机器准备 wheelhouse：

```bash
mkdir -p wheelhouse
python -m pip download -d wheelhouse qwen-asr
```

如果 Win10 需要 CUDA 版 PyTorch，建议在联网机器按本机 CUDA 版本单独准备 PyTorch wheel，再一起拷贝到离线机器。macOS M4 通常直接使用 PyTorch 的 macOS arm64 wheel。

离线机器安装：

```bash
python -m venv .venv-qwen3-asr
source .venv-qwen3-asr/bin/activate
python -m pip install --no-index --find-links wheelhouse qwen-asr
```

Win10 PowerShell：

```powershell
py -3.12 -m venv .venv-qwen3-asr
.\.venv-qwen3-asr\Scripts\Activate.ps1
python -m pip install --no-index --find-links wheelhouse qwen-asr
```

macOS M4 推荐使用 Python 3.12 arm64，例如 Miniforge 或 Homebrew Python。CPU 调试能跑但很慢。

## 单独测试 Python CLI

```bash
export HF_HUB_OFFLINE=1
export TRANSFORMERS_OFFLINE=1
export HF_DATASETS_OFFLINE=1

python scaffold-test/scaffold-test-qwen3-asr/scripts/qwen3_asr_cli.py \
  --model-path /data/models/Qwen3-ASR-0.6B \
  --audio /data/audio/test.wav \
  --language Chinese
```

macOS M4：

```bash
scaffold-test/scaffold-test-qwen3-asr/scripts/run-offline-macos.sh \
  /data/models/Qwen3-ASR-0.6B \
  /data/audio/test.wav \
  Chinese
```

Win10 PowerShell：

```powershell
.\scaffold-test\scaffold-test-qwen3-asr\scripts\run-offline-win10.ps1 `
  -ModelDir D:\models\Qwen3-ASR-0.6B `
  -AudioFile D:\audio\test.wav `
  -Language Chinese
```

需要强制 CPU 时，加参数 `--device-map cpu --dtype float32`。

## 启动 Spring Boot 测试服务

本模块会自动查找模块目录下的 `.venv`、`models/Qwen3-ASR-0.6B` 和推理脚本。首次在本机运行时创建 Python 环境：

```bash
cd scaffold-test/scaffold-test-qwen3-asr
uv venv --python 3.12 .venv
UV_CACHE_DIR=target/uv-cache uv pip install --python .venv/bin/python 'qwen-asr==0.0.6'
cd ../..

./mvnw -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples -DskipTests package
java -jar scaffold-test/scaffold-test-qwen3-asr/target/scaffold-test-qwen3-asr-1.0-SNAPSHOT.jar
```

需要使用其他 Python 环境或模型时，可通过 `QWEN3_ASR_PYTHON` 和 `QWEN3_ASR_MODEL_PATH` 覆盖自动发现结果。

在 IntelliJ IDEA 中可直接运行或调试 `Qwen3AsrApplication`，工作目录设置为仓库根目录或当前模块目录均可。

Win10 PowerShell：

```powershell
$env:QWEN3_ASR_PYTHON = "D:\envs\qwen3-asr\Scripts\python.exe"
$env:QWEN3_ASR_MODEL_PATH = "D:\models\Qwen3-ASR-0.6B"
$env:QWEN3_ASR_FORCED_ALIGNER_PATH = "D:\models\Qwen3-ForcedAligner-0.6B"

.\mvnw.cmd -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples -DskipTests package
java -jar scaffold-test\scaffold-test-qwen3-asr\target\scaffold-test-qwen3-asr-1.0-SNAPSHOT.jar
```

macOS M4：

```bash
export QWEN3_ASR_DEVICE_MAP=auto
export QWEN3_ASR_DTYPE=auto

./mvnw -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples -DskipTests package
java -jar scaffold-test/scaffold-test-qwen3-asr/target/scaffold-test-qwen3-asr-1.0-SNAPSHOT.jar
```

上传音频：

```bash
curl -F audio=@/data/audio/test.wav \
  -F language=Chinese \
  http://localhost:8093/qwen3-asr/transcribe
```

健康检查：

```bash
curl http://127.0.0.1:8093/qwen3-asr/health
```

模型尚未加载时返回：

```json
{"code":200,"msg":null,"data":{"status":"UP","modelStatus":"NOT_LOADED"}}
```

成功完成首次转写后，`modelStatus` 为 `READY`。

## Docker 部署

模块提供两个镜像目标，两者均支持 `linux/amd64`（x64）和 `linux/arm64`：

| 目标 | PyTorch | 默认设备 | 适用环境 |
| --- | --- | --- | --- |
| `cpu` | CPU wheel | `cpu` / `float32` | x64、Apple Silicon Docker、ARM Linux |
| `gpu` | CUDA 12.9 wheel | `cuda:0` / `float16` | x64 或 ARM64 NVIDIA Linux 服务器 |

GPU 版需要 NVIDIA 驱动和 NVIDIA Container Toolkit。ARM64 GPU 使用 NVIDIA SBSA CUDA 镜像，不能直接用于 Jetson；Jetson 需要匹配具体 JetPack/L4T 版本重新制作运行时。Docker Desktop on macOS 无法把 MPS 暴露给 Linux 容器，应使用 ARM64 CPU 版。

### 模型打包模式

| 模式 | 构建参数 | 镜像内容 | 运行方式 |
| --- | --- | --- | --- |
| 外置模型（默认） | `EMBED_MODEL=false` | 仅运行环境，不包含权重 | 挂载宿主机模型目录 |
| 内置模型 | `EMBED_MODEL=true` | 包含指定模型权重 | 无需模型卷 |

内置模型通过 BuildKit named context 接收模型目录，因此模型可以位于仓库外。指定目录必须直接包含 `config.json` 和 `model*.safetensors`。

### 构建脚本

从模块目录执行。未指定 `--embed-model` 时默认构建外置模型镜像：

```bash
cd scaffold-test/scaffold-test-qwen3-asr

# CPU 外置模型镜像，架构默认跟随当前机器
./scripts/build-docker-image.sh \
  --variant cpu \
  --external-model \
  --tag scaffold-qwen3-asr:cpu-external

# CPU 内置模型镜像
./scripts/build-docker-image.sh \
  --variant cpu \
  --arch arm64 \
  --model-name Qwen3-ASR-0.6B \
  --embed-model ./models/Qwen3-ASR-0.6B \
  --tag scaffold-qwen3-asr:cpu-embedded

# NVIDIA GPU 外置模型镜像
./scripts/build-docker-image.sh \
  --variant gpu \
  --arch amd64 \
  --external-model \
  --tag scaffold-qwen3-asr:gpu-external
```

`--arch` 支持 `amd64`、`arm64`，`--variant` 支持 `cpu`、`gpu`。脚本会校验内置模型目录，并在镜像标签中记录是否包含模型。

### 运行时 Docker Compose

`docker-compose.yml` 是纯运行时文件，不会重新构建镜像。它提供四个 profile，并且一次只应启动一个：

| Profile | 默认镜像 | 模型来源 |
| --- | --- | --- |
| `cpu-external` | `scaffold-qwen3-asr:cpu-external` | 宿主机只读挂载 |
| `cpu-embedded` | `scaffold-qwen3-asr:cpu-embedded` | 镜像内部 |
| `gpu-external` | `scaffold-qwen3-asr:gpu-external` | 宿主机只读挂载 |
| `gpu-embedded` | `scaffold-qwen3-asr:gpu-embedded` | 镜像内部 |

运行 CPU 外置模型镜像：

```bash
QWEN3_ASR_MODEL_SOURCE=/absolute/path/to/Qwen3-ASR-0.6B \
QWEN3_ASR_MODEL_NAME=Qwen3-ASR-0.6B \
docker compose -f docker-compose.yml \
  --profile cpu-external up -d qwen3-asr-cpu-external
```

模型位于模块默认目录 `models/Qwen3-ASR-0.6B` 时，可以省略两个模型环境变量：

```bash
docker compose -f docker-compose.yml \
  --profile cpu-external up -d qwen3-asr-cpu-external
```

运行 CPU 内置模型镜像：

```bash
docker compose -f docker-compose.yml \
  --profile cpu-embedded up -d qwen3-asr-cpu-embedded
```

GPU 对应使用 `gpu-external` 或 `gpu-embedded` profile。需要更换镜像或端口时设置：

```bash
QWEN3_ASR_GPU_EXTERNAL_IMAGE=registry.example.com/qwen3-asr:gpu-external \
QWEN3_ASR_HOST_PORT=18093 \
docker compose -f docker-compose.yml \
  --profile gpu-external up -d qwen3-asr-gpu-external
```

查看日志和停止服务：

```bash
docker logs -f qwen3-asr
docker compose -f docker-compose.yml --profile cpu-external down
```

### 直接使用 Buildx

外置模型构建不需要传入模型上下文：

```bash
docker buildx build --load \
  --platform linux/amd64 \
  --target cpu \
  --build-arg EMBED_MODEL=false \
  --build-arg MODEL_NAME=Qwen3-ASR-0.6B \
  -f scaffold-test/scaffold-test-qwen3-asr/Dockerfile \
  -t scaffold-qwen3-asr:cpu-external .
```

内置模型通过 `--build-context model-context=<模型目录>` 传入，可以使用绝对路径：

```bash
docker buildx build --load \
  --platform linux/arm64 \
  --target cpu \
  --build-arg EMBED_MODEL=true \
  --build-arg MODEL_NAME=Qwen3-ASR-0.6B \
  --build-context model-context=/absolute/path/to/Qwen3-ASR-0.6B \
  -f scaffold-test/scaffold-test-qwen3-asr/Dockerfile \
  -t scaffold-qwen3-asr:cpu-embedded .
```

### 多架构镜像发布

`docker-bake.hcl` 支持 `EMBED_MODEL`、`MODEL_NAME` 和 `MODEL_SOURCE`。外置模型是默认模式：

```bash
cd scaffold-test/scaffold-test-qwen3-asr
IMAGE_REPOSITORY=registry.example.com/scaffold/qwen3-asr \
IMAGE_VERSION=1.0.0 \
EMBED_MODEL=false \
MODEL_NAME=Qwen3-ASR-0.6B \
docker buildx bake --push
```

发布内置模型镜像时增加模型路径：

```bash
EMBED_MODEL=true \
MODEL_NAME=Qwen3-ASR-0.6B \
MODEL_SOURCE=/absolute/path/to/Qwen3-ASR-0.6B \
docker buildx bake --push
```

### 离线导出与搬运

离线包始终使用内置模型，避免目标机器再次下载。模型路径是必填参数：

```bash
scripts/export-offline-bundle.sh \
  cpu amd64 Qwen3-ASR-0.6B \
  ./models/Qwen3-ASR-0.6B \
  ./dist/qwen3-asr-cpu-amd64
```

输出目录包含 `image.tar.gz`、`bundle.env`、`import.sh` 和 `SHA256SUMS`。在同架构目标机器执行：

```bash
./import.sh . 8093
curl http://127.0.0.1:8093/qwen3-asr/health
```

本机 CPU ARM64 实测，外置模型镜像约 2.45 GB，内置 Qwen3-ASR-0.6B 后约 4.33 GB。

### 接口测试

```bash
curl -F audio=@/data/audio/test.wav \
  -F language=Chinese \
  http://127.0.0.1:8093/qwen3-asr/transcribe
```

接口文档地址为 `http://127.0.0.1:8093/doc.html`。


## 参考

- Qwen3-ASR 官方仓库：https://github.com/QwenLM/Qwen3-ASR
- 量子位文章：https://www.qbitai.com/2026/01/374421.html
