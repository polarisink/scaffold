package com.scaffold.orm;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.scaffold.orm.BaseAuditable.GMT_CREATED;
import static com.scaffold.orm.BaseAuditable.GMT_MODIFIED;


/**
 * 默认填充器,只填充时间，如需要插入用户信息，则重写该bean即可
 */
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, GMT_CREATED, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, GMT_MODIFIED, LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, GMT_MODIFIED, LocalDateTime.class, LocalDateTime.now());
    }
}
