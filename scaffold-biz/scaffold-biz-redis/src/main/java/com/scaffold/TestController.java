package com.scaffold;

import com.scaffold.redis.utils.RedisMQSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TestController {
    @GetMapping
    public void send() {
        RedisMQSender.send("user", new User("polaris", 18));
    }
}
