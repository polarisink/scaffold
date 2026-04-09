package com.scaffold;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "file-storage.enabled=false",
        "swagger.enabled=false"
})
class BizApplicationTests {

    @Test
    void contextLoads() {
    }
}
