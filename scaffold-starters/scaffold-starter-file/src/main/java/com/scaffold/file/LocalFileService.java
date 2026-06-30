package com.scaffold.file;

import lombok.extern.slf4j.Slf4j;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class LocalFileService implements FileUploadService {

    private final FileStorageProperties.Local local;
    private final Path baseDirPath;

    public LocalFileService(FileStorageProperties properties) {
        this.local = properties.getLocal();
        this.baseDirPath = Paths.get(local.getBasePath()).toAbsolutePath().normalize();
        try {
            if (!Files.exists(baseDirPath)) {
                Files.createDirectories(baseDirPath);
                log.info("本地存储目录创建成功: {}", baseDirPath);
            }
        } catch (IOException e) {
            log.error("本地存储目录创建失败: {}", baseDirPath, e);
            throw new RuntimeException("无法初始化本地存储目录", e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String originalFilename, String contentType, long contentLength) {
        String fileKey = generateFileKey(originalFilename);
        return uploadToPath(inputStream, fileKey, contentType, contentLength);
    }

    @Override
    public String uploadToPath(InputStream inputStream, String fileKey, String contentType, long contentLength) {
        Path targetPath = baseDirPath.resolve(fileKey);

        try (inputStream) {
            Path normalizedTargetPath = targetPath.toAbsolutePath().normalize();
            if (!normalizedTargetPath.startsWith(baseDirPath)) {
                throw new IllegalArgumentException("文件存储路径非法: " + fileKey);
            }
            Path parent = normalizedTargetPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.copy(inputStream, normalizedTargetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            log.info("文件上传成功，存储路径: {}", normalizedTargetPath);
            return local.getAccessPath().replace("**", "") + fileKey;
        } catch (IOException e) {
            log.error("本地文件上传失败: {}", fileKey, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public List<FolderUploadFileResult> uploadFolder(FolderUploadRequest request) throws IOException {
        List<FolderUploadSupport.UploadFile> files = FolderUploadSupport.resolveFiles(request);
        List<FolderUploadFileResult> results = new ArrayList<>(files.size());
        for (FolderUploadSupport.UploadFile file : files) {
            try (InputStream inputStream = Files.newInputStream(file.sourcePath())) {
                String accessPath = uploadToPath(
                        inputStream, file.storagePath(), file.contentType(), file.contentLength());
                results.add(FolderUploadSupport.result(file, accessPath));
            }
        }
        return results;
    }

    @Override
    public Optional<InputStream> download(String fileKey) {
        Path filePath = baseDirPath.resolve(fileKey);
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            try {
                return Optional.of(new FileInputStream(filePath.toFile()));
            } catch (FileNotFoundException e) {
                log.warn("文件下载失败，未找到文件: {}", fileKey);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            return Files.deleteIfExists(baseDirPath.resolve(fileKey));
        } catch (IOException e) {
            log.error("本地文件删除失败: {}", fileKey, e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "LocalFS";
    }

    private static String generateFileKey(String originalFilename) {
        String ext = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            ext = originalFilename.substring(lastDot);
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }
}
