package com.scaffold.file;

import com.scaffold.file.vo.FileDownload;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class S3FileService implements FileUploadService {

    private final FileStorageProperties.S3 s3Config;
    private final S3Client s3Client;

    public S3FileService(FileStorageProperties properties) {
        this.s3Config = properties.getS3();
        try {
            this.s3Client = S3Client.builder()
                    .endpointOverride(new URI(s3Config.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))
                    .region(Region.of(s3Config.getRegion()))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .expectContinueEnabled(false)
                            .build())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
            try {
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                        .bucket(s3Config.getBucketName())
                        .build();
                s3Client.headBucket(headBucketRequest);
                log.info("Bucket '{}' 已存在.", s3Config.getBucketName());
            } catch (NoSuchBucketException e) {
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

    S3FileService(FileStorageProperties properties, S3Client s3Client) {
        this.s3Config = properties.getS3();
        this.s3Client = s3Client;
    }

    @Override
    public String upload(InputStream inputStream, String originalFilename, String contentType, long contentLength) {
        String fileKey = extractOriginalFilename(originalFilename);
        return uploadToPath(inputStream, fileKey, contentType, contentLength);
    }

    @Override
    public String uploadToPath(InputStream inputStream, String fileKey, String contentType, long contentLength) {
        try (inputStream) {
            return putObject(fileKey, contentType, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (Exception e) {
            log.error("文件上传失败: {}", fileKey, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public List<FolderUploadFileResult> uploadFolder(FolderUploadRequest request) throws IOException {
        List<FolderUploadSupport.UploadFile> files = FolderUploadSupport.resolveFiles(request);
        List<FolderUploadFileResult> results = new ArrayList<>(files.size());
        for (FolderUploadSupport.UploadFile file : files) {
            try {
                String accessPath = putObject(file.storagePath(), file.contentType(), RequestBody.fromFile(file.sourcePath()));
                results.add(FolderUploadSupport.result(file, accessPath));
            } catch (Exception e) {
                log.error("文件夹上传失败: {}", file.sourcePath(), e);
                throw new IOException("文件夹上传失败: " + file.sourcePath(), e);
            }
        }
        return results;
    }

    private String putObject(String fileKey, String contentType, RequestBody requestBody) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();
        s3Client.putObject(putObjectRequest, requestBody);
        return fileKey;
    }

    @Override
    public Optional<FileDownload> download(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(fileKey)
                    .build();
            ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
            GetObjectResponse response = inputStream.response();
            long contentLength = response.contentLength() == null ? -1 : response.contentLength();
            return Optional.of(new FileDownload(
                    inputStream,
                    extractOriginalFilename(fileKey),
                    response.contentType(),
                    contentLength));
        } catch (NoSuchKeyException e) {
            log.warn("文件下载失败，未找到文件: {}", fileKey);
            return Optional.empty();
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileKey, e);
            throw new FileStorageException("文件读取失败: " + fileKey, e);
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
    public StorageType getStorageType() {
        return StorageType.S3;
    }

    private static String extractOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("原始文件名不能为空");
        }
        String normalized = originalFilename.replace('\\', '/');
        String filename = normalized.substring(normalized.lastIndexOf('/') + 1);
        if (filename.isBlank() || ".".equals(filename) || "..".equals(filename)) {
            throw new IllegalArgumentException("原始文件名非法: " + originalFilename);
        }
        return filename;
    }
}
