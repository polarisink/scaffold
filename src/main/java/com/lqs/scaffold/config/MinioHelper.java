package com.lqs.scaffold.config;

import com.lqs.scaffold.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author lqs
 * @date 2022/3/18
 */
//@Component
public class MinioHelper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IFileService iFileService;

    public MinioHelper(IFileService iFileService) {
        this.iFileService = iFileService;
    }

    /**
     * 上传
     *
     * @param filename
     */
    public void upload(String filename) {
        Assert.notNull(filename, "filename is null.");
        iFileService.upload(filename);
    }

    /**
     * 上传
     *
     * @param filename
     * @param object
     */
    public void upload(String filename, String object) {
        Assert.notNull(filename, "filename is null.");
        Assert.notNull(object, "object is null.");
        iFileService.upload(filename, object);
    }

    /**
     * 上传
     *
     * @param filename
     * @param object
     * @param bucket
     */
    public void upload(String filename, String object, String bucket) {
        Assert.notNull(filename, "filename is null.");
        Assert.notNull(object, "object is null.");
        Assert.notNull(bucket, "bucket is null.");
        iFileService.upload(filename, object, bucket);
    }
}
