# Whisper 会议录音转写示例

本模块展示会议录音处理链路：上传音频、预处理、调用本地 `whisper.cpp`、缓存转写结果、异步生成会议记录及发布完成/失败事件。

## 本地依赖

- 可执行文件：默认 `./whisper/whisper`
- Whisper 模型：默认 `models/ggml-medium.bin`
- 音频临时目录：默认 `/tmp/audio`
- `ffmpeg`：预处理非 WAV 音频时需要

可通过 `whisper.executable.path`、`whisper.model.path` 和 `audio.preprocess.path` 覆盖路径。

## 当前状态

这是服务层原型，不是完整可运行应用：模块尚无 `@SpringBootApplication`；`MeetingRecordController` 也未注册为 Spring Bean，且部分响应仍是占位返回值。应先补齐入口、Controller 注解、持久化和错误响应，再对外提供接口。

代码中预留的接口为 `POST /generate`、`POST /batch-generate` 和 `GET /{id}`。构建验证：

```bash
./mvnw -pl scaffold-test/scaffold-test-audio -am -Pexamples package
```
