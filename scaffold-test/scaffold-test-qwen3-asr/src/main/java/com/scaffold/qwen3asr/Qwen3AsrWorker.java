package com.scaffold.qwen3asr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 管理常驻的 Python 推理进程，并通过 stdin/stdout 与其交换 JSON Lines 消息。
 *
 * <p>模型首次转写时才加载，后续请求复用同一进程，避免每次请求重复加载模型。</p>
 */
@Component
public class Qwen3AsrWorker {

    private final Qwen3AsrProperties properties;
    private final ObjectMapper objectMapper;
    private final ExecutorService ioExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Qwen3AsrWorker(Qwen3AsrProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 向 Worker 发送一次转写请求。
     *
     * <p>方法使用同步锁保证一个请求的写入和响应读取不会与另一个请求交叉；
     * 当前 Python Worker 也按行串行处理请求。</p>
     */
    public synchronized Qwen3AsrResult transcribe(String audioPath, String language, boolean returnTimeStamps) {
        ensureStarted();
        try {
            writer.write(objectMapper.writeValueAsString(Map.of(
                    "audio", audioPath,
                    "language", language == null ? "" : language,
                    "returnTimeStamps", returnTimeStamps
            )));
            writer.newLine();
            writer.flush();

            JsonNode response = readMessage("result");
            if (!response.path("ok").asBoolean()) {
                throw new Qwen3AsrException("Qwen3-ASR 本地推理失败: " + response.path("error").asText("未知错误"));
            }
            return objectMapper.treeToValue(response.path("result"), Qwen3AsrResult.class);
        } catch (IOException e) {
            stop();
            throw new Qwen3AsrException("与 Qwen3-ASR 本地推理进程通信失败" + errorDetails(), e);
        }
    }

    public synchronized boolean isRunning() {
        return process != null && process.isAlive();
    }

    private void ensureStarted() {
        if (isRunning()) {
            return;
        }
        validateConfiguration();
        errorOutput.reset();

        try {
            ProcessBuilder builder = new ProcessBuilder(command());
            builder.environment().putAll(offlineEnvironment());
            process = builder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
            // stderr 不能混入 stdout 的 JSON 协议；单独持续消费也可防止子进程管道写满阻塞。
            ioExecutor.submit(() -> copyErrors(process.getErrorStream()));

            // ready 消息表示 Python 依赖和本地模型均已成功加载。
            JsonNode ready = readMessage("ready");
            if (!ready.path("ok").asBoolean()) {
                stop();
                throw new Qwen3AsrException("Qwen3-ASR 模型加载失败: " + ready.path("error").asText("未知错误"));
            }
        } catch (IOException e) {
            stop();
            throw new Qwen3AsrException("启动 Qwen3-ASR 本地推理进程失败" + errorDetails(), e);
        }
    }

    private JsonNode readMessage(String expectedType) throws IOException {
        // readLine 本身无超时能力，交给虚拟线程后由 Future 施加统一超时。
        Future<String> future = ioExecutor.submit(reader::readLine);
        try {
            String line = future.get(properties.timeoutSeconds(), TimeUnit.SECONDS);
            if (line == null) {
                stop();
                throw new Qwen3AsrException("Qwen3-ASR 本地推理进程意外退出" + errorDetails());
            }
            JsonNode message = objectMapper.readTree(line);
            if (!expectedType.equals(message.path("type").asText())) {
                stop();
                throw new Qwen3AsrException("Qwen3-ASR 返回了无效响应: " + line);
            }
            return message;
        } catch (TimeoutException e) {
            future.cancel(true);
            stop();
            throw new Qwen3AsrException("Qwen3-ASR 本地推理超时: " + Duration.ofSeconds(properties.timeoutSeconds()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop();
            throw new Qwen3AsrException("Qwen3-ASR 本地推理被中断", e);
        } catch (ExecutionException e) {
            stop();
            Throwable cause = e.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new Qwen3AsrException("读取 Qwen3-ASR 响应失败", cause);
        }
    }

    private List<String> command() {
        // 使用参数列表而不是拼接命令字符串，路径中的空格不会被 shell 二次解析。
        List<String> command = new ArrayList<>();
        command.add(properties.python());
        command.add(properties.script());
        command.add("--worker");
        command.add("--model-path");
        command.add(properties.modelPath());
        command.add("--device-map");
        command.add(properties.deviceMap());
        command.add("--dtype");
        command.add(properties.dtype());
        command.add("--max-inference-batch-size");
        command.add(String.valueOf(properties.maxInferenceBatchSize()));
        command.add("--max-new-tokens");
        command.add(String.valueOf(properties.maxNewTokens()));
        if (properties.forcedAlignerPath() != null && !properties.forcedAlignerPath().isBlank()) {
            command.add("--forced-aligner-path");
            command.add(properties.forcedAlignerPath());
        }
        return command;
    }

    private void validateConfiguration() {
        if (properties.modelPath() == null || properties.modelPath().isBlank()) {
            throw new Qwen3AsrException("请配置 qwen3-asr.model-path 为本地 Qwen3-ASR 模型目录");
        }
        if (!Files.isDirectory(Path.of(properties.modelPath()))) {
            throw new Qwen3AsrException("模型目录不存在: " + properties.modelPath());
        }
        if (!Files.isRegularFile(Path.of(properties.script()))) {
            throw new Qwen3AsrException("Qwen3-ASR Python 脚本不存在: " + properties.script());
        }
        if (properties.forcedAlignerPath() != null && !properties.forcedAlignerPath().isBlank()
                && !Files.isDirectory(Path.of(properties.forcedAlignerPath()))) {
            throw new Qwen3AsrException("Forced Aligner 模型目录不存在: " + properties.forcedAlignerPath());
        }
    }

    private static Map<String, String> offlineEnvironment() {
        // 强制各模型库只读取本地文件，防止服务运行时意外访问 Hugging Face Hub。
        return Map.of(
                "HF_HUB_OFFLINE", "1",
                "TRANSFORMERS_OFFLINE", "1",
                "HF_DATASETS_OFFLINE", "1",
                "PYTHONIOENCODING", "UTF-8"
        );
    }

    private void copyErrors(InputStream inputStream) {
        try (inputStream) {
            inputStream.transferTo(errorOutput);
        } catch (IOException ignored) {
            // 进程退出或 stop() 关闭流时会到达这里，不覆盖原始推理异常。
        }
    }

    private String errorDetails() {
        String details = errorOutput.toString(StandardCharsets.UTF_8).trim();
        return details.isEmpty() ? "" : ": " + details;
    }

    private synchronized void stop() {
        if (process != null) {
            process.destroy();
            try {
                // 先给 Python 进程正常清理资源的机会，超时后再强制结束。
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }
        process = null;
        reader = null;
        writer = null;
    }

    @PreDestroy
    void destroy() {
        // Spring 容器关闭时同步回收子进程和虚拟线程执行器。
        stop();
        ioExecutor.shutdownNow();
    }
}
