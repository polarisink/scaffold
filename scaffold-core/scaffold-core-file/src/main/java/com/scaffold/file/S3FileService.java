// src/main/java/.../service/S3CompatibleFileService.java
package com.scaffold.file;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class S3FileService implements FileUploadService {

    private final FileUploadProperties.S3 s3Config;
    private final S3Client s3Client;

    public S3FileService(FileUploadProperties properties) {
        this.s3Config = properties.getS3();
        try {
            this.s3Client = S3Client.builder()
                    .endpointOverride(new URI(s3Config.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))
                    .region(Region.of(s3Config.getRegion()))
                    .forcePathStyle(true) // 关键：S3 兼容存储通常需要路径风格
                    .build();

            // 检查并创建 Bucket
            try {
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                        .bucket(s3Config.getBucketName())
                        .build();
                s3Client.headBucket(headBucketRequest);
                log.info("Bucket '{}' 已存在.", s3Config.getBucketName());
            } catch (NoSuchBucketException e) {
                // Bucket 不存在，创建它
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(s3Config.getBucketName())
                        .build();
                s3Client.createBucket(createBucketRequest);
                log.info("Bucket '{}' 创建成功.", s3Config.getBucketName());
            }
        } catch (Exception e) {
            log.error("客户端初始化失败", e);
            throw new RuntimeException(" 服务连接失败", e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String originalFilename, String contentType) {
        String fileKey = generateFileKey(originalFilename);

        try (inputStream) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(fileKey)
                    .contentType(contentType)
                    .build();

            // AWS SDK V2 使用 RequestBody
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            return fileKey;
        } catch (Exception e) {
            log.error("文件上传失败: {}", originalFilename, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public Optional<InputStream> download(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(fileKey)
                    .build();

            // AWS SDK V2 返回 SdkBytes
            return Optional.of(s3Client.getObject(getObjectRequest));
        } catch (NoSuchKeyException e) {
            log.warn("文件下载失败，未找到文件: {}", fileKey);
            return Optional.empty();
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileKey, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileKey, e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "s3";
    }

    private String generateFileKey(String originalFilename) {
        String ext = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            ext = originalFilename.substring(lastDot);
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }
}