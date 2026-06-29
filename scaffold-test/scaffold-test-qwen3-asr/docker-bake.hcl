variable "IMAGE_REPOSITORY" {
  default = "scaffold-qwen3-asr"
}

variable "IMAGE_VERSION" {
  default = "latest"
}

variable "MODEL_NAME" {
  default = "Qwen3-ASR-0.6B"
}

variable "MODEL_SOURCE" {
  default = "./models/Qwen3-ASR-0.6B"
}

variable "EMBED_MODEL" {
  default = "false"
}

group "default" {
  # 默认一次构建并标记 CPU、GPU 两种运行镜像。
  targets = ["cpu", "gpu"]
}

target "common" {
  context    = "../.."
  dockerfile = "scaffold-test/scaffold-test-qwen3-asr/Dockerfile"
  # CPU 镜像支持双架构；GPU 镜像能否跨架构取决于 CUDA 基础镜像。
  platforms  = ["linux/amd64", "linux/arm64"]
  contexts = {
    "model-context" = MODEL_SOURCE
  }
  args = {
    EMBED_MODEL = EMBED_MODEL
    MODEL_NAME   = MODEL_NAME
  }
}

target "cpu" {
  inherits = ["common"]
  target   = "cpu"
  tags     = ["${IMAGE_REPOSITORY}:${IMAGE_VERSION}-cpu"]
}

target "gpu" {
  inherits = ["common"]
  target   = "gpu"
  tags     = ["${IMAGE_REPOSITORY}:${IMAGE_VERSION}-gpu"]
}
