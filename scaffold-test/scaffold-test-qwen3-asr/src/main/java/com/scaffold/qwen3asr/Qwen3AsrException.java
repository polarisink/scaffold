package com.scaffold.qwen3asr;

/**
 * Qwen3-ASR 参数校验、进程启动和推理通信的统一业务异常。
 */
public class Qwen3AsrException extends RuntimeException {

    public Qwen3AsrException(String message) {
        super(message);
    }

    public Qwen3AsrException(String message, Throwable cause) {
        super(message, cause);
    }
}
