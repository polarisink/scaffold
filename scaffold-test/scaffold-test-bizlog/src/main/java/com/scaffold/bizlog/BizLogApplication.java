package com.scaffold.bizlog;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableLogRecord(tenant = "com.scaffold.test")
@SpringBootApplication
public class BizLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizLogApplication.class, args);
    }

}
