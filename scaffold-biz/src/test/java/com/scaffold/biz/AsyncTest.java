package com.scaffold.biz;

import com.scaffold.biz.asyc.AsyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class AsyncTest {
    @Autowired
    AsyncService asyncService;

    @Test
    void run() {
        IntStream.range(0, 10000).forEach(asyncService::age);
    }
}
