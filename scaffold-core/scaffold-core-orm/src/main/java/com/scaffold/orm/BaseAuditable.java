package com.scaffold.orm;

import com.baomidou.mybatisplus.annotation.*;
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
 * 可审计基础类
 *
 * @author aries
 * @since 2022/09/10
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditable {

    public static final String GMT_MODIFIED = "gmtModified";
    public static final String GMT_CREATED = "gmtCreated";
    public static final String CREATED_BY = "createdBy";
    public static final String MODIFIED_BY = "modifiedBy";

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    protected Long id;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @LastModifiedDate
    @Column(columnDefinition = "datetime comment '更新时间'")
    protected LocalDateTime gmtModified;
    /**
     * 创建时间
     */
    @CreatedDate
    @TableField(fill = FieldFill.INSERT)
    @Column(columnDefinition = "datetime comment '创建时间'")
    protected LocalDateTime gmtCreated;

    /**
     * 创造者id
     */
    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    @Column(columnDefinition = "bigint(11) comment '创建者id'")
    protected Long createdBy;

    /**
     * 修改者id
     */
    @LastModifiedBy
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Column(columnDefinition = "bigint(11) comment '修改者id'")
    protected Long modifiedBy;

    /**
     * 逻辑删除字段
     */
    @JsonIgnore
    @TableLogic(value = "0", delval = "1")
    @Column(columnDefinition = "int not null default 0 comment '逻辑删除'")
    private Integer deleted = 0;
}
