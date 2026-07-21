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
