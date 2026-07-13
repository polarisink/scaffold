package com.scaffold.file;

import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 文件存储服务契约。
 */
public interface FileUploadService {

    String upload(InputStream inputStream, String originalFilename, String contentType, long contentLength);

    String uploadToPath(InputStream inputStream, String fileKey, String contentType, long contentLength);

    List<FolderUploadFileResult> uploadFolder(FolderUploadRequest request) throws IOException;

    Optional<InputStream> download(String fileKey);

    boolean delete(String fileKey);

    String getStorageType();
}
