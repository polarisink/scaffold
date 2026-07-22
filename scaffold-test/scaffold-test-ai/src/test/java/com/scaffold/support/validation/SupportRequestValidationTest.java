package com.scaffold.support.validation;

import com.scaffold.support.assistant.ChatRequest;
import com.scaffold.support.workorder.CreateWorkOrderRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证阶段一至七请求对象的 Bean Validation 约束。 */
class SupportRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesChatRequest() {
        assertThat(validator.validate(new ChatRequest(null, "正常消息"))).isNotEmpty();
        assertThat(validator.validate(new ChatRequest(1L, "  "))).isNotEmpty();
        assertThat(validator.validate(new ChatRequest(1L, "x".repeat(4_001)))).isNotEmpty();
        assertThat(validator.validate(new ChatRequest(1L, "正常消息"))).isEmpty();
    }

    @Test
    void validatesCreateWorkOrderRequest() {
        assertThat(validator.validate(new CreateWorkOrderRequest("bad", "正常描述"))).isNotEmpty();
        assertThat(validator.validate(new CreateWorkOrderRequest("request_0001", "  "))).isNotEmpty();
        assertThat(validator.validate(new CreateWorkOrderRequest("request_0001", "x".repeat(4_001)))).isNotEmpty();
        assertThat(validator.validate(new CreateWorkOrderRequest("request_0001", "正常描述"))).isEmpty();
    }
}
