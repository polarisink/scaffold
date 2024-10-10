package com.scaffold.biz.rbac.module.mapper;

import com.scaffold.biz.module.rbac.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SysUserMapperTest {

    @Autowired
    SysUserMapper userMapper;

    @Test
    void list() {
    }
}