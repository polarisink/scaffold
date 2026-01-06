package com.scaffold.file;

import com.scaffold.base.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@ConditionalOnBean(FileUploadService.class)
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        return R.success(fileUploadService.upload(file.getInputStream(), file.getOriginalFilename(), file.getContentType()));
    }
}
