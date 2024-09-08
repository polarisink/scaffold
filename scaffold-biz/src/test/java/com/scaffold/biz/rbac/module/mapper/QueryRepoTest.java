package com.scaffold.biz.rbac.module.mapper;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.scaffold.biz.module.rbac.entity.SysMenu;
import com.scaffold.biz.module.rbac.mapper.QueryRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueryRepoTest {

    @Autowired
    QueryRepo repo;

    @Test
    void findMenuCollByUserId() {
        EasyPageResult<SysMenu> menuCollByUserId = repo.findMenuCollByUserId(111L);
    }
}