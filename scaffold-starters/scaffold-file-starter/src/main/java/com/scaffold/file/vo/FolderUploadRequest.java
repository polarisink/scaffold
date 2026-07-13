package com.scaffold.file.vo;

import lombok.Data;

import java.util.List;

@Data
public class FolderUploadRequest {

    private String uploadFolder;
    private Boolean preserveHierarchy;
    private List<String> ignoreFileList;
    private String storagePrefixPath;
}
