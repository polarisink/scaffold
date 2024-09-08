package com.scaffold.biz.asyc;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    @Async
    public void age(int age) {
        if (age % 3 == 0) {
            throw new RuntimeException("error age");
        }
        System.out.println(Thread.currentThread() + " age is " + age);
    }
}
