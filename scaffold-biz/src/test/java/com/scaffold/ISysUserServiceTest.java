package com.scaffold;

import com.scaffold.rbac.service.SysUserService;
import com.scaffold.rbac.vo.user.SysUserCreateVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:scaffold-biz-user-service;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class ISysUserServiceTest {
    @Autowired
    SysUserService sysUserService;

    @Test
    void save() {
        SysUserCreateVO vo = new SysUserCreateVO("test-user", "admin", 1L, List.of(1L));
        sysUserService.save(vo);
    }
}
