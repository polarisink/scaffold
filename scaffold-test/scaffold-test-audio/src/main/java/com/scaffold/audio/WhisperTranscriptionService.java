package com.scaffold.audio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class WhisperTranscriptionService {
    @Value("${whisper.model.path:models/ggml-medium.bin}") // 使用whisper.cpp模型
    private String modelPath;

    @Value("${whisper.executable.path:./whisper/whisper}")
    private String whisperExecutable;

    /**
     * 使用Whisper进行语音识别
     */
    public TranscriptionResult transcribeAudio(String audioFilePath, String language) {
        try {
            String outputFileName = "transcript_" + System.currentTimeMillis();
            String outputPath = System.getProperty("java.io.tmpdir") + "/" + outputFileName;

            // 构建Whisper命令
            List<String> cmd = new ArrayList<>();
            cmd.add(whisperExecutable);
            cmd.add("--model");
            cmd.add(modelPath);
            cmd.add("--output-txt");
            cmd.add("--output-file");
            cmd.add(outputPath);
            cmd.add("--language");
            cmd.add(language != null ? language : "zh"); // 默认中文
            cmd.add(audioFilePath);

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();

            // 等待处理完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Whisper处理失败，退出码: {}", exitCode);
                return TranscriptionResult.failure("语音识别失败，退出码: " + exitCode);
            }

            // 读取识别结果
            String txtFilePath = outputPath + ".txt";
            String transcript = readFileToString(txtFilePath);

            log.info("语音识别完成: {}", audioFilePath);
            return TranscriptionResult.success(transcript);

        } catch (Exception e) {
            log.error("语音识别过程出错", e);
            return TranscriptionResult.failure("语音识别失败: " + e.getMessage());
        }
    }

    /**
     * 批量处理音频文件
     */
    public List<TranscriptionResult> batchTranscribe(List<String> audioFiles, String language) {
        return audioFiles.parallelStream()
                .map(filePath -> transcribeAudio(filePath, language))
                .collect(Collectors.toList());
    }

    private String readFileToString(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
}
