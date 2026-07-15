package com.scaffold.file;

import com.scaffold.file.vo.FileDownload;
import com.scaffold.file.vo.FolderUploadFileResult;
import com.scaffold.file.vo.FolderUploadRequest;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class S3FileServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldReportS3StorageType() {
        FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
        S3FileService service = new S3FileService(properties, mock(S3Client.class));

        assertThat(service.getStorageType()).isEqualTo(StorageType.S3);
    }

    @Test
    void shouldKeepOriginalFilenameAndRemoveClientPath() {
        S3Client s3Client = mock(S3Client.class);
        FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
        properties.getS3().setBucketName("test-bucket");
        S3FileService service = new S3FileService(properties, s3Client);
        byte[] content = "content".getBytes(StandardCharsets.UTF_8);

        String fileKey = service.upload(
                new ByteArrayInputStream(content),
                "C:\\fakepath\\原始报告.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content.length);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), org.mockito.ArgumentMatchers.any(RequestBody.class));
        assertThat(fileKey).isEqualTo("原始报告.xlsx");
        assertThat(requestCaptor.getValue().key()).isEqualTo("原始报告.xlsx");
    }

    @Test
    void shouldDisableExpectContinueForCompatibleS3Endpoints() throws Exception {
        AtomicReference<String> expectHeader = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            if ("PUT".equals(exchange.getRequestMethod())) {
                expectHeader.set(exchange.getRequestHeaders().getFirst("Expect"));
            }
            exchange.getRequestBody().transferTo(java.io.OutputStream.nullOutputStream());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        try {
            FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
            FileStorageProperties.S3 s3 = properties.getS3();
            s3.setEndpoint("http://127.0.0.1:" + server.getAddress().getPort());
            s3.setAccessKey("access-key");
            s3.setSecretKey("secret-key");
            s3.setBucketName("test-bucket");

            S3FileService service = new S3FileService(properties);
            byte[] content = "content".getBytes(StandardCharsets.UTF_8);
            service.uploadToPath(
                    new ByteArrayInputStream(content), "test.txt", "text/plain", content.length);

            assertThat(expectHeader.get()).isNull();
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldUploadFolderWithHierarchyPrefixAndExactContentLength() throws Exception {
        Path nestedFolder = Files.createDirectories(tempDir.resolve("docs"));
        Files.writeString(tempDir.resolve("root.txt"), "root");
        Files.writeString(nestedFolder.resolve("guide.txt"), "guide-content");
        Files.writeString(nestedFolder.resolve("ignored.tmp"), "ignored");

        S3Client s3Client = mock(S3Client.class);
        FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
        properties.getS3().setBucketName("test-bucket");
        S3FileService service = new S3FileService(properties, s3Client);

        FolderUploadRequest request = new FolderUploadRequest();
        request.setUploadFolder(tempDir.toString());
        request.setStoragePrefixPath("backup");
        request.setPreserveHierarchy(true);
        request.setIgnoreFileList(List.of(".*\\.tmp"));

        List<FolderUploadFileResult> results = service.uploadFolder(request);

        assertThat(results)
                .extracting(FolderUploadFileResult::getStoragePath)
                .containsExactlyInAnyOrder("backup/root.txt", "backup/docs/guide.txt");
        assertThat(results)
                .extracting(FolderUploadFileResult::getAccessPath)
                .containsExactlyInAnyOrder("backup/root.txt", "backup/docs/guide.txt");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(2)).putObject(requestCaptor.capture(), bodyCaptor.capture());

        assertThat(requestCaptor.getAllValues())
                .extracting(PutObjectRequest::bucket)
                .containsOnly("test-bucket");
        Map<String, Long> contentLengthByKey = java.util.stream.IntStream
                .range(0, requestCaptor.getAllValues().size())
                .boxed()
                .collect(Collectors.toMap(
                        index -> requestCaptor.getAllValues().get(index).key(),
                        index -> bodyCaptor.getAllValues().get(index).contentLength()));
        assertThat(contentLengthByKey)
                .containsEntry("backup/root.txt", Files.size(tempDir.resolve("root.txt")))
                .containsEntry("backup/docs/guide.txt", Files.size(nestedFolder.resolve("guide.txt")));
    }

    @Test
    void shouldReturnDownloadMetadata() throws Exception {
        S3Client s3Client = mock(S3Client.class);
        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> inputStream = mock(ResponseInputStream.class);
        when(inputStream.response()).thenReturn(GetObjectResponse.builder()
                .contentType("application/pdf")
                .contentLength(42L)
                .build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(inputStream);
        FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
        properties.getS3().setBucketName("test-bucket");
        S3FileService service = new S3FileService(properties, s3Client);

        FileDownload download = service.download("reports/annual.pdf").orElseThrow();

        assertThat(download.inputStream()).isSameAs(inputStream);
        assertThat(download.filename()).isEqualTo("annual.pdf");
        assertThat(download.contentType()).isEqualTo("application/pdf");
        assertThat(download.contentLength()).isEqualTo(42L);
    }

    @Test
    void shouldThrowStorageExceptionWhenS3ReadFails() {
        S3Client s3Client = mock(S3Client.class);
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(SdkClientException.create("connection failed"));
        FileStorageProperties properties = new FileStorageProperties(null, null, null, null, null);
        properties.getS3().setBucketName("test-bucket");
        S3FileService service = new S3FileService(properties, s3Client);

        assertThatThrownBy(() -> service.download("report.pdf"))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("report.pdf");
    }
}
