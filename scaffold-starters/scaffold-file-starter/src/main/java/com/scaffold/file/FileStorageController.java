package com.scaffold.file;

import com.scaffold.file.vo.FileDownload;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@ConditionalOnBean(FileUploadService.class)
@Tag(name = "文件上传")
public class FileStorageController {

    private final FileUploadService fileUploadService;

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

    @Operation(summary = "文件下载")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam String fileKey) {
        validateFileKey(fileKey);
        FileDownload download;
        try {
            download = fileUploadService.download(fileKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文件不存在"));
        } catch (FileStorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "文件读取失败", e);
        }
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.filename(), StandardCharsets.UTF_8)
                .build();
        ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(parseContentType(download.contentType()));
        if (download.contentLength() >= 0) {
            response.contentLength(download.contentLength());
        }
        return response.body(new InputStreamResource(download.inputStream()));
    }

    private static void validateFileKey(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件存储键不能为空");
        }
        String normalized = fileKey.replace('\\', '/');
        String filename = normalized.substring(normalized.lastIndexOf('/') + 1);
        if (filename.isBlank() || ".".equals(filename) || "..".equals(filename)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件存储键非法");
        }
    }

    private static MediaType parseContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
