package com.scaffold.security.util;

import com.scaffold.base.exception.BaseException;
import com.scaffold.security.vo.PayloadDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    @Test
    void shouldGenerateAndResolveToken() {
        PayloadDTO payloadDTO = PayloadDTO.of(1L, "aries", List.of("admin"));

        String token = JwtUtil.generateToken(payloadDTO);
        PayloadDTO resolved = JwtUtil.resolveToken(token);

        assertThat(token).isNotBlank();
        assertThat(resolved.getUserId()).isEqualTo(1L);
        assertThat(resolved.getUsername()).isEqualTo("aries");
        assertThat(resolved.getAuthorities()).containsExactly("admin");
        assertThat(resolved.getExp()).isGreaterThan(resolved.getIat());
    }

    @Test
    void shouldRejectExpiredToken() {
        PayloadDTO payloadDTO = PayloadDTO.of(2L, "expired", List.of("user"));
        long now = Instant.now().getEpochSecond();
        payloadDTO.setIat(now - 10);
        payloadDTO.setExp(now - 1);

        String token = JwtUtil.generateToken(payloadDTO);

        assertThatThrownBy(() -> JwtUtil.resolveToken(token))
                .isInstanceOf(BaseException.class)
                .hasMessage("token已过期");
    }

    @Test
    void shouldExtractBearerTokenCaseInsensitively() {
        String token = "abc.def.ghi";

        assertThat(JwtUtil.getRealToken("Bearer " + token)).isEqualTo(token);
        assertThat(JwtUtil.getRealToken("bearer " + token)).isEqualTo(token);
        assertThat(JwtUtil.getRealToken(token)).isNull();
        assertThat(JwtUtil.getRealToken("Bearer")).isNull();
    }
}
