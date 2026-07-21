package com.scaffold.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scaffold.biz.entity.Order;
import com.scaffold.orm.MyBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends MyBaseMapper<Order> {
    default Order selectByOrderName(String orderName) {
        return selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderName, orderName));
    }
}
