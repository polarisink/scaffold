package com.scaffold.support.refund;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * 退款确认流程的时间源配置，便于稳定测试过期行为。
 */
@Configuration(proxyBeanMethods = false)
public class RefundConfiguration {

    @Bean
    Clock refundClock() {
        return Clock.systemUTC();
    }
}
