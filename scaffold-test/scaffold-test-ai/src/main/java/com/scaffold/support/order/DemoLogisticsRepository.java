package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoLogistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 演示物流数据的 JPA 数据访问接口。
 */
public interface DemoLogisticsRepository extends JpaRepository<DemoLogistics, String> {

    /**
     * 判断指定订单是否已经初始化物流记录。
     */
    boolean existsByOrderNo(String orderNo);

    /**
     * 按订单号查询物流事实。
     */
    Optional<DemoLogistics> findByOrderNo(String orderNo);
}
