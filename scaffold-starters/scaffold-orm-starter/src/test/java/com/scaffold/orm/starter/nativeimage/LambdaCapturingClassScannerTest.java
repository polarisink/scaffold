package com.scaffold.orm.starter.nativeimage;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LambdaCapturingClassScannerTest {

    @Test
    void discoversOnlyClassesThatCaptureSerializableLambdas() throws Exception {
        Path classesDirectory = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

        Set<String> classNames = new LambdaCapturingClassScanner().scan(classesDirectory);

        assertThat(classNames)
                .contains(MapperWithLambda.class.getName())
                .doesNotContain(PlainMapper.class.getName());
    }

    @Test
    void discoversClassesThroughTheApplicationClassLoader() throws Exception {
        URL classesDirectory = getClass().getProtectionDomain().getCodeSource().getLocation();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{classesDirectory}, null)) {
            assertThat(new LambdaCapturingClassScanner().scan(classLoader))
                    .contains(MapperWithLambda.class.getName());
        }
    }

    interface MapperWithLambda {
        default SFunction<TestEntity, String> nameColumn() {
            return TestEntity::name;
        }
    }

    interface PlainMapper {
    }

    record TestEntity(String name) {
    }
}
