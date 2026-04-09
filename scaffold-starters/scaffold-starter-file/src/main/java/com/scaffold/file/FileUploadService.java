package com.scaffold.file;

import java.io.InputStream;
import java.util.Optional;

public interface FileUploadService {

    String upload(InputStream inputStream, String originalFilename, String contentType);

    Optional<InputStream> download(String fileKey);

    boolean delete(String fileKey);

    String getStorageType();
}
