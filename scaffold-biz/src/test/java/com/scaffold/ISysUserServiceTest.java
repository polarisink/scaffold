package com.scaffold;

import com.scaffold.rbac.service.ISysUserService;
import com.scaffold.rbac.vo.user.SysUserCreateVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ISysUserServiceTest {
    @Autowired
    ISysUserService sysUserService;

    @Test
    void save() {
        SysUserCreateVO vo = new SysUserCreateVO("admin", "admin", 1L, List.of(1L));
        sysUserService.save(vo);
    }
}
