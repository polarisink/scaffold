package com.scaffold.file;

import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import com.scaffold.file.vo.FileAccessInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@ConditionalOnBean(FileUploadService.class)
@Tag(name = "文件上传")
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileStorageProperties properties;

    @Operation(summary = "文件访问配置")
    @GetMapping("/access")
    public FileAccessInfo access() {
        return new FileAccessInfo(fileUploadService.getStorageType(), resolveAccessPrefix());
    }

    @Operation(summary = "文件上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        return fileUploadService.upload(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize());
    }

    @Operation(summary = "文件夹上传")
    @PostMapping("/upload-folder")
    public List<FolderUploadFileResult> uploadFolder(@RequestBody FolderUploadRequest request) throws IOException {
        return fileUploadService.uploadFolder(request);
    }

    private String resolveAccessPrefix() {
        if (hasText(properties.getAccessPrefix())) {
            return normalizePrefix(properties.getAccessPrefix());
        }
        if (properties.getType() == FileStorageProperties.StorageType.LOCAL) {
            return normalizePrefix(properties.getLocal().getAccessPath().replace("**", ""));
        }
        return "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizePrefix(String prefix) {
        String normalized = prefix.trim();
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

}
