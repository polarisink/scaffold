package com.scaffold.rbac.components;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.scaffold.security.vo.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.scaffold.orm.BaseAuditable.*;


/**
 * 带用户信息的插入
 */
@Primary
@Component
public class RbacMetaObjectHandler implements MetaObjectHandler, AuditorAware<Long> {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, GMT_CREATED, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, GMT_MODIFIED, LocalDateTime.class, now);
        Long userId = LoginUser.userId();
        this.strictInsertFill(metaObject, CREATED_BY, Long.class, userId);
        this.strictInsertFill(metaObject, MODIFIED_BY, Long.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, GMT_MODIFIED, LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, MODIFIED_BY, Long.class, LoginUser.userId());
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(LoginUser.userId());
    }
}
