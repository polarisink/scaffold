package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoOrderRepository extends JpaRepository<DemoOrder, String> {
}
