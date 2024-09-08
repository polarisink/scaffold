package com.scaffold.biz;

import com.scaffold.biz.module.rbac.entity.SysUser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionalTest {
    @Test
    void nullable() {
        SysUser user = null;
        System.out.println(Optional.ofNullable(user).map(SysUser::getDeleted).map(String::valueOf).orElse("null"));
    }
}
