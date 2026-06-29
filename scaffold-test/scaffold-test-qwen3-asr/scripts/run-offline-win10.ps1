param(
    [Parameter(Mandatory = $true)]
    [string]$ModelDir,

    [Parameter(Mandatory = $true)]
    [string]$AudioFile,

    [string]$Language = "Chinese"
)

$ErrorActionPreference = "Stop"

# 优先使用环境变量指定的虚拟环境解释器。
if (-not $env:QWEN3_ASR_PYTHON) {
    $env:QWEN3_ASR_PYTHON = "python"
}

# 禁止依赖库在推理时联网下载模型或数据集。
$env:HF_HUB_OFFLINE = "1"
$env:TRANSFORMERS_OFFLINE = "1"
$env:HF_DATASETS_OFFLINE = "1"
$env:PYTHONIOENCODING = "UTF-8"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
& $env:QWEN3_ASR_PYTHON "$ScriptDir\qwen3_asr_cli.py" `
    --model-path $ModelDir `
    --audio $AudioFile `
    --language $Language `
    --device-map auto `
    --dtype auto
