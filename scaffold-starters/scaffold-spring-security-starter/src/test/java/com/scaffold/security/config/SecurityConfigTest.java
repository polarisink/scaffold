package com.scaffold.security.config;

import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.LoginUser;
import com.scaffold.security.vo.PayloadDTO;
import com.scaffold.security.vo.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SecurityConfigTest {

    @Test
    void treatsAnonymousAuthenticationAsNoCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "anonymous-key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        try {
            assertThat(LoginUser.userId()).isNull();
            assertThat(LoginUser.currentUser()).isNull();
            assertThat(LoginUser.username()).isNull();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void onlyLoginEndpointIsIgnoredByDefault() {
        SecurityProperties properties = new SecurityProperties();

        assertThat(properties.getIgnoreList())
                .contains("/auth/login")
                .doesNotContain("/auth/*", "/auth/logout");
    }

    @Test
    void returnsUnauthorizedHttpStatusAndStructuredErrorBody() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig(
                mock(UserDetailsService.class),
                mock(TokenAuthenticationFilter.class),
                new SecurityProperties());
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityConfig.authenticationEntryPoint().commence(
                new MockHttpServletRequest("GET", "/user/context"),
                response,
                new InsufficientAuthenticationException("Token 已过期"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains(
                "\"code\":40100",
                "\"message\":\"Token 已过期\"",
                "\"data\":null");
    }

    @Test
    void returnsUnauthorizedWhenStoredTokenHasExpired() throws Exception {
        TokenStore tokenStore = mock(TokenStore.class);
        JwtUtil jwtUtil = new JwtUtil("0123456789abcdef0123456789abcdef");
        String token = jwtUtil.generateToken(PayloadDTO.of(1L, "tester", List.of("USER")));
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter(
                new AntPathMatcher(), tokenStore, new SecurityProperties(), jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/context");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filterChain = mock(jakarta.servlet.FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains(
                "\"code\":40102",
                "\"message\":\"Token 已过期\"",
                "\"data\":null");
        verifyNoInteractions(filterChain);
    }
}
