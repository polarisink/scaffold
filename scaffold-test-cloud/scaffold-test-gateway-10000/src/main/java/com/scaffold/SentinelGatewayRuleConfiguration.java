package com.scaffold;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Configuration
public class SentinelGatewayRuleConfiguration implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) {
        GatewayRuleManager.loadRules(Set.of(
                new GatewayFlowRule("cloud-provider").setCount(5).setIntervalSec(1),
                new GatewayFlowRule("cloud-consumer").setCount(5).setIntervalSec(1)));

        GatewayCallbackManager.setBlockHandler((exchange, throwable) -> ServerResponse
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of(
                        "code", HttpStatus.TOO_MANY_REQUESTS.value(),
                        "message", "gateway route is limited by Sentinel",
                        "path", exchange.getRequest().getPath().value(),
                        "timestamp", Instant.now().toString()))));
    }
}
