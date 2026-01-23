package com.scaffold.audio;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class AudioPreprocessingService {

    @Value("${audio.preprocess.path:/tmp/audio}")
    private String tempPath;

    /**
     * 使用FFmpeg预处理音频文件
     */
    public String preprocessAudio(String inputFilePath) throws IOException {
        // 创建临时文件
        File inputFile = new File(inputFilePath);
        String outputFileName = "preprocessed_" + System.currentTimeMillis() + ".wav";
        String outputPath = tempPath + "/" + outputFileName;

        // FFmpeg命令：转换为Whisper推荐的格式（16kHz, 单声道, WAV）
        String[] cmd = {
                "ffmpeg",
                "-i", inputFilePath,
                "-ar", "16000", // 采样率16kHz
                "-ac", "1",   // 单声道
                "-c:a", "pcm_s16le",// 编码格式
                outputPath
        };

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("音频预处理完成: {} -> {}", inputFilePath, outputPath);
                return outputPath;
            } else {
                throw new IOException("FFmpeg处理失败，退出码: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("音频处理被中断", e);
        }
    }

    /**
     * 验证音频文件基本信息
     */
    public AudioFileInfo getAudioInfo(String filePath) throws IOException {
        String[] cmd = {
                "ffprobe",
                "-v", "quiet",
                "-show_format",
                "-show_streams",
                "-print_format", "json",
                filePath
        };

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();

        String result = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        ).lines().collect(Collectors.joining("\n"));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(result);

        // 解析音频信息
        JsonNode streams = rootNode.get("streams");
        for (JsonNode stream : streams) {
            if ("audio".equals(stream.get("codec_type").asText())) {
                return AudioFileInfo.builder()
                        .duration(stream.get("duration").asDouble())
                        .sampleRate(stream.get("sample_rate").asInt())
                        .channels(stream.get("channels").asInt())
                        .codec(stream.get("codec_name").asText())
                        .build();
            }
        }

        return null;
    }
}
