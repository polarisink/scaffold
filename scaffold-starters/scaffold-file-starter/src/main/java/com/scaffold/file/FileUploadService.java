package com.scaffold.file;

import com.scaffold.file.vo.FileDownload;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 文件上传与存储服务契约。
 *
 * <p>屏蔽本地文件系统、S3 兼容对象存储等底层实现的差异，统一提供文件上传、
 * 文件夹上传、下载和删除能力。上传方法会在读取完成后关闭传入的输入流；
 * 下载方法返回的输入流则由调用方负责关闭。</p>
 */
public interface FileUploadService {

    /**
     * 上传单个文件，并由存储实现根据原始文件名确定存储键。
     *
     * @param inputStream      待上传文件的输入流，方法返回后会被关闭
     * @param originalFilename 原始文件名
     * @param contentType      文件的 MIME 类型
     * @param contentLength    文件长度，单位为字节
     * @return 文件的访问路径或存储键，具体格式由存储实现决定
     * @throws IllegalArgumentException 原始文件名等参数不合法时抛出
     * @throws RuntimeException         文件存储失败时抛出
     */
    String upload(InputStream inputStream, String originalFilename, String contentType, long contentLength);

    /**
     * 将单个文件上传到指定的存储路径。
     *
     * @param inputStream   待上传文件的输入流，方法返回后会被关闭
     * @param fileKey       目标存储键或相对路径
     * @param contentType   文件的 MIME 类型
     * @param contentLength 文件长度，单位为字节
     * @return 文件的访问路径或存储键，具体格式由存储实现决定
     * @throws IllegalArgumentException 存储路径不合法时抛出
     * @throws RuntimeException         文件存储失败时抛出
     */
    String uploadToPath(InputStream inputStream, String fileKey, String contentType, long contentLength);

    /**
     * 按请求配置批量上传指定文件夹中的文件。
     *
     * @param request 文件夹上传请求，包含源目录、层级保留规则、忽略列表和存储前缀
     * @return 各文件的上传结果，顺序与解析后的文件顺序一致
     * @throws IOException 遍历、读取或上传文件失败时抛出
     */
    List<FolderUploadFileResult> uploadFolder(FolderUploadRequest request) throws IOException;

    /**
     * 根据存储键下载文件。
     *
     * @param fileKey 文件的存储键或相对路径
     * @return 文件存在且可读时返回下载信息，否则返回 {@link Optional#empty()}；
     * 下载信息中的输入流由调用方负责关闭
     * @throws FileStorageException 存储后端读取失败时抛出
     */
    Optional<FileDownload> download(String fileKey);

    /**
     * 根据存储键删除文件。
     *
     * @param fileKey 文件的存储键或相对路径
     * @return 存储后端确认删除请求成功时返回 {@code true}，否则返回 {@code false}
     */
    boolean delete(String fileKey);

    /**
     * 获取当前文件存储实现的类型标识。
     *
     * @return 存储类型标识，例如 {@code LocalFS} 或 {@code s3}
     */
    StorageType getStorageType();
}
