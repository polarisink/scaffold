package com.scaffold.security.util;

import com.scaffold.base.exception.BaseException;
import com.scaffold.security.vo.PayloadDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";
    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @Test
    void shouldRejectMissingOrShortSecret() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new JwtUtil(null))
                .withMessage("security.token.jwt.secret 不能为空");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new JwtUtil("too-short"))
                .withMessage("security.token.jwt.secret 长度不能少于 32 字节");
    }

    @Test
    void shouldGenerateAndResolveToken() {
        PayloadDTO payloadDTO = PayloadDTO.of(1L, "aries", List.of("admin"));

        String token = jwtUtil.generateToken(payloadDTO);
        PayloadDTO resolved = jwtUtil.resolveToken(token);

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

        String token = jwtUtil.generateToken(payloadDTO);

        assertThatThrownBy(() -> jwtUtil.resolveToken(token))
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
