package com.scaffold.cloud.dubbo.consumer;

import com.scaffold.cloud.dubbo.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GreetingController {

    @DubboReference(check = false)
    private GreetingService greetingService;

    @GetMapping("/api/dubbo/greet")
    String greet(@RequestParam(defaultValue = "world") String name) {
        return greetingService.greet(name);
    }
}
