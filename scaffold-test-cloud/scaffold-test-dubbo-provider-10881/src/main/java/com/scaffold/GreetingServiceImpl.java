package com.scaffold;

import com.scaffold.cloud.dubbo.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
class GreetingServiceImpl implements GreetingService {

    @Override
    public String greet(String name) {
        return "Hello, " + name + " from Dubbo provider";
    }
}
