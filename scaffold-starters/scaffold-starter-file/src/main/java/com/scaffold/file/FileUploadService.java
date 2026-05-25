package com.scaffold.file;

import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface FileUploadService {

    private static Path resolveUploadFolder(FolderUploadRequest request) {
        if (request == null || request.getUploadFolder() == null || request.getUploadFolder().isBlank()) {
            throw new IllegalArgumentException("被上传文件夹不能为空");
        }
        try {
            Path uploadFolder = Path.of(request.getUploadFolder().trim()).toRealPath();
            if (!Files.isDirectory(uploadFolder)) {
                throw new IllegalArgumentException("被上传路径不是文件夹: " + request.getUploadFolder());
            }
            return uploadFolder;
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("被上传文件夹路径非法: " + request.getUploadFolder(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException("被上传文件夹不存在或不可访问: " + request.getUploadFolder(), e);
        }
    }

    private static List<Pattern> compileIgnorePatterns(List<String> ignoreFileList) {
        if (ignoreFileList == null || ignoreFileList.isEmpty()) {
            return List.of();
        }
        return ignoreFileList.stream().filter(pattern -> pattern != null && !pattern.isBlank()).map(Pattern::compile).collect(Collectors.toList());
    }

    private static boolean isIgnored(Path uploadFolder, Path file, List<Pattern> ignorePatterns) {
        if (ignorePatterns.isEmpty()) {
            return false;
        }
        String relativePath = toStoragePath(uploadFolder.relativize(file));
        String filename = file.getFileName().toString();
        return ignorePatterns.stream().anyMatch(pattern -> pattern.matcher(relativePath).matches() || pattern.matcher(filename).matches());
    }

    private static String buildUploadPath(String storagePrefixPath, Path relativePath, boolean preserveHierarchy) {
        String filePath = preserveHierarchy ? toStoragePath(relativePath) : relativePath.getFileName().toString();
        filePath = normalizeStoragePath(filePath);
        if (storagePrefixPath.isBlank()) {
            return filePath;
        }
        return storagePrefixPath + "/" + filePath;
    }

    private static String normalizeStoragePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        String value = path.trim().replace("\\", "/");
        List<String> segments = new ArrayList<>();
        for (String segment : value.split("/")) {
            if (segment.isBlank() || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                throw new IllegalArgumentException("文件存储路径不能包含上级目录: " + path);
            }
            segments.add(segment);
        }
        return String.join("/", segments);
    }

    private static String toStoragePath(Path path) {
        return path.toString().replace("\\", "/");
    }

    String upload(InputStream inputStream, String originalFilename, String contentType);

    default String uploadToPath(InputStream inputStream, String fileKey, String contentType) {
        throw new UnsupportedOperationException("当前文件存储实现不支持指定路径上传");
    }

    default List<FolderUploadFileResult> uploadFolder(FolderUploadRequest request) throws IOException {
        Path uploadFolder = resolveUploadFolder(request);
        List<Pattern> ignorePatterns = compileIgnorePatterns(request.getIgnoreFileList());
        List<Path> files;
        try (Stream<Path> pathStream = Files.walk(uploadFolder)) {
            files = pathStream.filter(Files::isRegularFile).filter(path -> !isIgnored(uploadFolder, path, ignorePatterns)).toList();
        }

        String storagePrefixPath = normalizeStoragePath(request.getStoragePrefixPath());
        List<FolderUploadFileResult> results = new ArrayList<>(files.size());
        for (Path file : files) {
            Path relativePath = uploadFolder.relativize(file);
            String uploadPath = buildUploadPath(storagePrefixPath, relativePath, Boolean.TRUE.equals(request.getPreserveHierarchy()));
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            try (InputStream inputStream = Files.newInputStream(file)) {
                String accessPath = uploadToPath(inputStream, uploadPath, contentType);
                FolderUploadFileResult result = new FolderUploadFileResult();
                result.setSourcePath(file.toString());
                result.setStoragePath(uploadPath);
                result.setAccessPath(accessPath);
                results.add(result);
            }
        }
        return results;
    }

    Optional<InputStream> download(String fileKey);

    boolean delete(String fileKey);

    String getStorageType();
}
