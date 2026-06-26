package com.scaffold.rbac.auth;

import java.util.List;

public record RbacLoginUser(Long userId, String username, List<String> roleCodeList) {
}
