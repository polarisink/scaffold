package com.scaffold.biz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "biz_order")
@TableName("biz_order")
public class Order extends BaseLongAuditable {
    private String orderName;
}
