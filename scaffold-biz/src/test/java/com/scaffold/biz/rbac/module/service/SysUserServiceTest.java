package com.scaffold.biz.rbac.module.service;

import com.scaffold.biz.module.rbac.service.SysUserService;
import com.scaffold.biz.module.rbac.vo.user.SysUserCreateVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SysUserServiceTest {
    @Autowired
    SysUserService userService;

    @Test
    void save() {
        userService.save(new SysUserCreateVO("aaa", "aaa", "1", "1", List.of(1L, 2L)));
    }
}