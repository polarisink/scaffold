package com.scaffold.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SentinelRuleConfiguration implements ApplicationRunner {

    static final String CONSUMER_PROVIDER_ECHO = "consumer-provider-echo";

    @Override
    public void run(ApplicationArguments args) {
        FlowRuleManager.loadRules(List.of(
                new FlowRule(CONSUMER_PROVIDER_ECHO)
                        .setGrade(RuleConstant.FLOW_GRADE_QPS)
                        .setCount(2)));

        DegradeRuleManager.loadRules(List.of(
                new DegradeRule(CONSUMER_PROVIDER_ECHO)
                        .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO)
                        .setCount(0.5)
                        .setMinRequestAmount(5)
                        .setStatIntervalMs(10_000)
                        .setTimeWindow(10)));
    }
}
