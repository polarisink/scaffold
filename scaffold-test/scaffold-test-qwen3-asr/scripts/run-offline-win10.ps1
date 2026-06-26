param(
    [Parameter(Mandatory = $true)]
    [string]$ModelDir,

    [Parameter(Mandatory = $true)]
    [string]$AudioFile,

    [string]$Language = "Chinese"
)

$ErrorActionPreference = "Stop"

if (-not $env:QWEN3_ASR_PYTHON) {
    $env:QWEN3_ASR_PYTHON = "python"
}

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
