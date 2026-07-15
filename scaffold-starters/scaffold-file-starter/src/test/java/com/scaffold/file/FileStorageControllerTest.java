package com.scaffold.file;

import com.scaffold.file.vo.FileDownload;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileStorageControllerTest {

    private final FileUploadService fileUploadService = mock(FileUploadService.class);
    private final FileStorageController controller = new FileStorageController(fileUploadService);

    @Test
    void shouldDownloadFileAsAttachment() throws Exception {
        byte[] content = "file-content".getBytes(StandardCharsets.UTF_8);
        when(fileUploadService.download("reports/report.txt"))
                .thenReturn(Optional.of(new FileDownload(
                        new ByteArrayInputStream(content), "report.txt", "text/plain", content.length)));

        ResponseEntity<InputStreamResource> response = controller.download("reports/report.txt");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .contains("attachment").contains("report.txt");
        assertThat(response.getHeaders().getContentType()).hasToString("text/plain");
        assertThat(response.getHeaders().getContentLength()).isEqualTo(content.length);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInputStream().readAllBytes()).isEqualTo(content);
    }

    @Test
    void shouldReturnNotFoundWhenFileDoesNotExist() {
        when(fileUploadService.download("missing.txt")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.download("missing.txt"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(404));
    }

    @Test
    void shouldRejectFileKeyWithoutFilename() {
        assertThatThrownBy(() -> controller.download("reports/"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(400));
    }

    @Test
    void shouldRejectBlankFileKey() {
        assertThatThrownBy(() -> controller.download(" "))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(400));
    }

    @Test
    void shouldReturnServerErrorWhenStorageReadFails() {
        when(fileUploadService.download("report.txt"))
                .thenThrow(new FileStorageException("storage unavailable", new IOException("connection reset")));

        assertThatThrownBy(() -> controller.download("report.txt"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(500));
    }
}
