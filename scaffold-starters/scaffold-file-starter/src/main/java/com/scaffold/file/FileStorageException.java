package com.scaffold.file;

/**
 * 文件存储后端访问失败。
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
