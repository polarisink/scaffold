package com.scaffold.rbac.auth.security.webflux;

public record SecurityWebFluxTokenInfo(String tokenName, String tokenValue, Long userId, String username) {
}
