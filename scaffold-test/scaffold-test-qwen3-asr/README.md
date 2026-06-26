# scaffold-test-qwen3-asr

Qwen3-ASR 离线本地语音识别测试模块。

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

```bash
export QWEN3_ASR_PYTHON=/opt/conda/envs/qwen3-asr/bin/python
export QWEN3_ASR_MODEL_PATH=/data/models/Qwen3-ASR-0.6B
export QWEN3_ASR_FORCED_ALIGNER_PATH=/data/models/Qwen3-ForcedAligner-0.6B

./mvnw -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples spring-boot:run
```

Win10 PowerShell：

```powershell
$env:QWEN3_ASR_PYTHON = "D:\envs\qwen3-asr\Scripts\python.exe"
$env:QWEN3_ASR_MODEL_PATH = "D:\models\Qwen3-ASR-0.6B"
$env:QWEN3_ASR_FORCED_ALIGNER_PATH = "D:\models\Qwen3-ForcedAligner-0.6B"

.\mvnw.cmd -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples spring-boot:run
```

macOS M4：

```bash
export QWEN3_ASR_PYTHON="$PWD/.venv-qwen3-asr/bin/python"
export QWEN3_ASR_MODEL_PATH="$HOME/models/Qwen3-ASR-0.6B"
export QWEN3_ASR_DEVICE_MAP=auto
export QWEN3_ASR_DTYPE=auto

./mvnw -pl scaffold-test/scaffold-test-qwen3-asr -am -Pexamples spring-boot:run
```

上传音频：

```bash
curl -F audio=@/data/audio/test.wav \
  -F language=Chinese \
  http://localhost:8093/qwen3-asr/transcribe
```

使用已有本地音频路径：

```bash
curl -X POST \
  -d audioPath=/data/audio/test.wav \
  -d language=Chinese \
  http://localhost:8093/qwen3-asr/transcribe-path
```

返回时间戳：

```bash
curl -F audio=@/data/audio/test.wav \
  -F language=Chinese \
  -F returnTimeStamps=true \
  http://localhost:8093/qwen3-asr/transcribe
```

## 参考

- Qwen3-ASR 官方仓库：https://github.com/QwenLM/Qwen3-ASR
- 量子位文章：https://www.qbitai.com/2026/01/374421.html
