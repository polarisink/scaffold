package com.scaffold.rbac.components;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.scaffold.security.vo.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * 带用户信息的插入
 */
@Component
public class RbacMetaObjectHandler implements MetaObjectHandler, AuditorAware<Long> {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "gmtCreated", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, now);
        Long userId = LoginUser.userId();
        this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
        this.strictInsertFill(metaObject, "modifiedBy", Long.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "modifiedBy", Long.class, LoginUser.userId());
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(LoginUser.userId());
    }
}
