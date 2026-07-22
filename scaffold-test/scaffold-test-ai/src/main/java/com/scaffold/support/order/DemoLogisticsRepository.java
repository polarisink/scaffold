package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoLogistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemoLogisticsRepository extends JpaRepository<DemoLogistics, String> {

    boolean existsByOrderNo(String orderNo);

    Optional<DemoLogistics> findByOrderNo(String orderNo);
}
