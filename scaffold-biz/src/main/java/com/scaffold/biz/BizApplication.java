package com.scaffold.biz;

import com.scaffold.biz.vo.MsgHead;
import io.netty.buffer.Unpooled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BizApplication {
    public static void main(String[] args) {
        MsgHead msgHead = new MsgHead(Unpooled.buffer());
        SpringApplication.run(BizApplication.class, args);
    }
}
