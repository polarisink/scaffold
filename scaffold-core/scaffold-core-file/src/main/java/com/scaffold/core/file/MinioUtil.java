package com.scaffold.core.file;

import cn.hutool.extra.spring.SpringUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * minio静态工具类
 *
 * @author lqsgo
 */
@Slf4j
@Component
public class MinioUtil {


    public static final MinioClient minioClient = SpringUtil.getBean(MinioClient.class);
    private static final MinioProperties minioProperties = SpringUtil.getBean(MinioProperties.class);

    /**
     * 文件上传
     *
     * @param file     文件
     * @param fileName 修饰过的文件名 非源文件名
     */
    public static String minioUpload(MultipartFile file, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            fileName = file.getOriginalFilename();
            if (fileName != null) {
                fileName = fileName.replaceAll(" ", "_");
            }
        }
        String finalFileName = fileName;
        return execute(() -> {
            try (InputStream inputStream = file.getInputStream()) {
                String bucketName = minioProperties.getBucket();
                PutObjectArgs objectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(finalFileName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build();
                minioClient.putObject(objectArgs);
                // 返回路径
                return getPreviewFileUrl(bucketName, finalFileName);
            }
        }, "MultipartFile minioUpload error");
    }

    /**
     * 文件上传
     *
     * @param file 文件
     */
    public static String minioUpload(MultipartFile file) {
        return minioUpload(file, null);
    }

    /**
     * 文件上传
     *
     * @param fileBytes 文件
     * @param fileName  修饰过的文件名 非源文件名
     */
    public static String minioUpload(byte[] fileBytes, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("文件名不可为空！");
        }
        return execute(() -> {
            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                int fileSize = fileBytes.length;
                String bucketName = minioProperties.getBucket();
                PutObjectArgs objectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, fileSize, -1)
                        .build();

                minioClient.putObject(objectArgs);
                return getPreviewFileUrl(bucketName, fileName);
            }
        }, "bytes minioUpload error");
    }

    /**
     * 获取文件绝对地址
     *
     * @param bucketName 桶
     * @param fileName   文件名称
     */
    public static String getPreviewFileUrl(String bucketName, String fileName) {
        return execute(() -> minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .build()), "minio getPreviewFileUrl error");
    }

    /**
     * bucket是否存在
     *
     * @param bucket bucket
     * @return 是否存在
     */
    public static boolean bucketExists(String bucket) {
        return execute(() -> {
            BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucket).build();
            return minioClient.bucketExists(existsArgs);
        }, "bucketExists error");

    }

    /**
     * 创建bucket
     *
     * @param bucket bucket
     */
    public static void makeBucket(String bucket) {
        execute(() -> {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucket).build();
            minioClient.makeBucket(makeBucketArgs);
            return null;
        }, "");
    }

    /**
     * 获取策略
     *
     * @return 策略
     */
    public static Map<String, String> getPolicy() {
        return execute(() -> {
            String bucket = minioProperties.getBucket();
            long hours = minioProperties.getTimeUnit().toHours(minioProperties.getTimeout());
            ZonedDateTime expiration = ZonedDateTime.now().plusHours(hours);
            PostPolicy postPolicy = new PostPolicy(bucket, expiration);
            postPolicy.addStartsWithCondition("key", minioProperties.getObjectNamePrefix());
            postPolicy.addContentLengthRangeCondition(1, 10485760);
            Map<String, String> result = minioClient.getPresignedPostFormData(postPolicy);
            result.put("url", minioProperties.getEndpoint() + "/" + bucket);
            return result;
        }, "");
    }

    private static <R> R execute(MinioThrowsExFunction<R> function, String msg) {
        try {
            return function.apply();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(msg);
        }
    }

    @PostConstruct
    public void initBucket() {
        if (!bucketExists(minioProperties.getBucket())) {
            makeBucket(minioProperties.getBucket());
        }
    }

    @FunctionalInterface
    public interface MinioThrowsExFunction<R> {
        R apply() throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
                InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException,
                XmlParserException;
    }
}
