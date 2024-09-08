package com.scaffold.biz.rbac.module.mapper;

import com.scaffold.biz.module.rbac.mapper.SysMenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SysMenuMapperTest {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Test
    void findByParentIdIn() {
        //测试通过id找祖宗id集合
    }

    @Test
    void son() {

    }

}