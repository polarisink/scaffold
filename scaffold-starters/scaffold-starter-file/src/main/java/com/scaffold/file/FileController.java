package com.scaffold.file;

import com.scaffold.base.util.R;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
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

    @Operation(summary = "文件上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        return R.success(fileUploadService.upload(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize()));
    }

    @Operation(summary = "文件夹上传")
    @PostMapping("/upload-folder")
    public List<FolderUploadFileResult> uploadFolder(@RequestBody FolderUploadRequest request) throws IOException {
        return fileUploadService.uploadFolder(request);
    }
}
