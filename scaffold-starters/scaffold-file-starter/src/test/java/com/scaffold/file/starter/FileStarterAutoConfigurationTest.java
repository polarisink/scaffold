package com.scaffold.file.starter;

import com.scaffold.file.FileController;
import com.scaffold.file.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class FileStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FileStarterAutoConfiguration.class));

    @Test
    void shouldNotExposeUploadBeansWhenStorageDisabled() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(FileUploadService.class);
            assertThat(context).doesNotHaveBean(FileController.class);
        });
    }

    @Test
    void shouldExposeLocalUploadBeansWhenStorageEnabled() {
        contextRunner
                .withPropertyValues(
                        "scaffold.file-storage.enabled=true",
                        "scaffold.file-storage.type=local",
                        "scaffold.file-storage.local.base-path=target/test-uploads"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(FileUploadService.class);
                    assertThat(context).hasSingleBean(FileController.class);
                });
    }
}
