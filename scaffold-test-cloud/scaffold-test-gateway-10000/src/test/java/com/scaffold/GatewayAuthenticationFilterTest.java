package com.scaffold;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayAuthenticationFilterTest {

    @Test
    void shouldSkipIgnoredPath() {
        AtomicBoolean authCalled = new AtomicBoolean(false);
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        GatewayAuthenticationFilter filter = filterWithAuthResponse(authCalled, """
                {"code":0,"data":{"loginId":1}}
                """);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/auth/login").build());

        filter.filter(exchange, chain(exchangeInChain -> {
            chainCalled.set(true);
            return Mono.empty();
        })).block();

        assertThat(authCalled).isFalse();
        assertThat(chainCalled).isTrue();
    }

    @Test
    void shouldRejectRequestWithoutAuthorization() {
        AtomicBoolean authCalled = new AtomicBoolean(false);
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        GatewayAuthenticationFilter filter = filterWithAuthResponse(authCalled, """
                {"code":0,"data":{"loginId":1}}
                """);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/provider/api/echo").build());

        filter.filter(exchange, chain(exchangeInChain -> {
            chainCalled.set(true);
            return Mono.empty();
        })).block();

        assertThat(authCalled).isFalse();
        assertThat(chainCalled).isFalse();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldValidateTokenAndForwardUserIdHeader() {
        AtomicBoolean authCalled = new AtomicBoolean(false);
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        GatewayAuthenticationFilter filter = filterWithAuthResponse(authCalled, """
                {"code":0,"data":{"loginId":10001}}
                """);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/provider/api/echo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .header("X-User-Id", "spoofed")
                        .build());

        filter.filter(exchange, chain(exchangeInChain -> {
            chainCalled.set(true);
            assertThat(exchangeInChain.getRequest().getHeaders().getFirst("X-User-Id"))
                    .isEqualTo("10001");
            return Mono.empty();
        })).block();

        assertThat(authCalled).isTrue();
        assertThat(chainCalled).isTrue();
    }

    private static GatewayAuthenticationFilter filterWithAuthResponse(AtomicBoolean authCalled, String body) {
        WebClient.Builder builder = WebClient.builder()
                .exchangeFunction(request -> {
                    authCalled.set(true);
                    assertThat(request.headers().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token");
                    return Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .body(body)
                            .build());
                });
        return new GatewayAuthenticationFilter(new GatewayAuthProperties(null, null, null), builder, new ObjectMapper());
    }

    private static GatewayFilterChain chain(GatewayFilterChain chain) {
        return chain;
    }
}
