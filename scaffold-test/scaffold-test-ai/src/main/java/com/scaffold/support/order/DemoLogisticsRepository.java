package com.scaffold.support.order;

import com.scaffold.support.order.model.DemoLogistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoLogisticsRepository extends JpaRepository<DemoLogistics, String> {
}
