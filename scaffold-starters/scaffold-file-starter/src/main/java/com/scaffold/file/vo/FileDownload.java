package com.scaffold.file.vo;

import java.io.InputStream;

/**
 * 文件下载信息。
 *
 * @param inputStream   文件内容输入流，由响应写出方负责关闭
 * @param filename      下载文件名
 * @param contentType   文件的 MIME 类型
 * @param contentLength 文件长度，未知时为 {@code -1}
 */
public record FileDownload(InputStream inputStream, String filename, String contentType, long contentLength) {
}
