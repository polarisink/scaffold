package com.scaffold;

import com.scaffold.redis.annotations.RedisStreamListener;
import com.scaffold.redis.domain.RedisMessage;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Consumer {
    @RedisStreamListener("user")
    public void test(User user) {
        System.out.println(user);
    }

    @RedisStreamListener("user")
    public void test2(RedisMessage<User> user) {
        System.out.println(user);
    }
}
