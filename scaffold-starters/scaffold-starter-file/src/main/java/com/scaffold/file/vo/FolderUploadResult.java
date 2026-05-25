package com.scaffold.file.vo;

import lombok.Data;

import java.util.List;

@Data
public class FolderUploadResult {

    private int total;
    private List<FolderUploadFileResult> files;
}
