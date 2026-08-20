package com.scaffold.orm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 可审计基础类，字符串主键
 *
 * @author aries
 * @since 2022/09/10
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseStringAuditable extends BaseAuditable {

    /**
     * id
     */
    @Id
    @TableId(type = IdType.ASSIGN_UUID)
    protected String id;
}
