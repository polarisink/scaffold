package com.scaffold.codegen.entity;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_gen")
public class TestGen extends BaseAuditable {
    @Column(columnDefinition = "varchar(60) not null comment '名字'", unique = true)
    private String name;

    @Column(columnDefinition = "int not null comment '名字'")
    private Integer age;
}
