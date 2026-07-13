package com.scaffold.file;

import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class FolderUploadSupport {

    private FolderUploadSupport() {
    }

    static List<UploadFile> resolveFiles(FolderUploadRequest request) throws IOException {
        Path uploadFolder = resolveUploadFolder(request);
        List<Pattern> ignorePatterns = compileIgnorePatterns(request.getIgnoreFileList());
        String storagePrefixPath = normalizeStoragePath(request.getStoragePrefixPath());
        boolean preserveHierarchy = Boolean.TRUE.equals(request.getPreserveHierarchy());

        List<Path> files;
        try (Stream<Path> pathStream = Files.walk(uploadFolder)) {
            files = pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> !isIgnored(uploadFolder, path, ignorePatterns))
                    .toList();
        }

        List<UploadFile> uploadFiles = new ArrayList<>(files.size());
        for (Path file : files) {
            Path relativePath = uploadFolder.relativize(file);
            String storagePath = buildUploadPath(storagePrefixPath, relativePath, preserveHierarchy);
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            uploadFiles.add(new UploadFile(file, storagePath, contentType, Files.size(file)));
        }
        return uploadFiles;
    }

    static FolderUploadFileResult result(UploadFile file, String accessPath) {
        FolderUploadFileResult result = new FolderUploadFileResult();
        result.setSourcePath(file.sourcePath().toString());
        result.setStoragePath(file.storagePath());
        result.setAccessPath(accessPath);
        return result;
    }

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
        return ignoreFileList.stream()
                .filter(pattern -> pattern != null && !pattern.isBlank())
                .map(Pattern::compile)
                .toList();
    }

    private static boolean isIgnored(Path uploadFolder, Path file, List<Pattern> ignorePatterns) {
        if (ignorePatterns.isEmpty()) {
            return false;
        }
        String relativePath = toStoragePath(uploadFolder.relativize(file));
        String filename = file.getFileName().toString();
        return ignorePatterns.stream().anyMatch(pattern ->
                pattern.matcher(relativePath).matches() || pattern.matcher(filename).matches());
    }

    private static String buildUploadPath(String storagePrefixPath, Path relativePath, boolean preserveHierarchy) {
        String filePath = preserveHierarchy
                ? toStoragePath(relativePath)
                : relativePath.getFileName().toString();
        filePath = normalizeStoragePath(filePath);
        return storagePrefixPath.isBlank() ? filePath : storagePrefixPath + "/" + filePath;
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

    record UploadFile(Path sourcePath, String storagePath, String contentType, long contentLength) {
    }
}
