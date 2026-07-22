package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemoOrderRepository extends JpaRepository<DemoOrder, String> {

    boolean existsByOrderNo(String orderNo);

    Optional<DemoOrder> findByOrderNo(String orderNo);
}
