package com.scaffold;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "scaffold.file-storage.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:scaffold-biz-context;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "swagger.enabled=false"
})
class BizApplicationTests {

    @Test
    void contextLoads() {
    }
}
