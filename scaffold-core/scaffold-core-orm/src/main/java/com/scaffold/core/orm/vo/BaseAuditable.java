package com.scaffold.core.orm.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 可审计基础
 *
 * @author aries
 * @since 2022/09/10
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditable {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * 修改时间
     */
    @LastModifiedDate
    @Column(columnDefinition = "datetime comment '更新时间'")
    protected LocalDateTime gmtModified;
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(columnDefinition = "datetime comment '创建时间'")
    protected LocalDateTime gmtCreated;
    /**
     * 创造者id
     */
    @CreatedBy
    @Column(columnDefinition = "bigint(11) comment '创建者id'")
    protected Long createdBy;
    /**
     * 修理者id
     */
    @LastModifiedBy
    @Column(columnDefinition = "bigint(11) comment '修改者id'")
    protected Long modifiedBy;
    /**
     * 逻辑删除字段
     */
    @JsonIgnore
    @Column(columnDefinition = "int not null default 0 comment '逻辑删除'")
    private Integer deleted;
}
