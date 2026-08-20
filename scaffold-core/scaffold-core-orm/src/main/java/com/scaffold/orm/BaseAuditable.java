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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditable implements Serializable {

    public static final String GMT_MODIFIED = "gmtModified";
    public static final String GMT_CREATED = "gmtCreated";
    public static final String CREATED_BY = "createdBy";
    public static final String MODIFIED_BY = "modifiedBy";
    public static final Set<String> AUDITABLE_FIELDS = Set.of(
            BaseAuditable.GMT_MODIFIED,
            BaseAuditable.GMT_CREATED,
            BaseAuditable.CREATED_BY,
            BaseAuditable.MODIFIED_BY
    );
    /**
     * 修改时间
     */
    @LastModifiedDate
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime gmtModified;
    /**
     * 创建时间
     */
    @CreatedDate
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime gmtCreated;

    /**
     * 创造者id
     */
    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    protected Long createdBy;

    /**
     * 修改者id
     */
    @LastModifiedBy
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Long modifiedBy;

    /**
     * 逻辑删除字段
     */
    @JsonIgnore
    @TableLogic(value = "0", delval = "1")
    @Column(nullable = false)
    private Integer deleted = 0;
}
