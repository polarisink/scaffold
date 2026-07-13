package com.scaffold.file.vo;

import lombok.Data;

@Data
public class FolderUploadFileResult {

    private String sourcePath;
    private String storagePath;
    private String accessPath;
}
