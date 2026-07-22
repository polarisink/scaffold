package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 演示订单数据的 JPA 数据访问接口。
 */
public interface DemoOrderRepository extends JpaRepository<DemoOrder, String> {

    /**
     * 判断指定订单号是否已存在。
     */
    boolean existsByOrderNo(String orderNo);

    /**
     * 按订单号读取订单事实。
     */
    Optional<DemoOrder> findByOrderNo(String orderNo);

    /**
     * 退款执行前以悲观写锁读取订单，防止并发重复修改。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select orders from DemoOrder orders where orders.orderNo = :orderNo")
    Optional<DemoOrder> findByOrderNoForUpdate(@Param("orderNo") String orderNo);
}
