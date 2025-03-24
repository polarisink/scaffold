package com.scaffold.rocket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface RocketRepository extends JpaRepository<Rocket, String> {

    @Query("SELECT r FROM Rocket r WHERE r.scheduledLaunchTime <= :currentTime AND r.launched = false")
    List<Rocket> findByScheduledLaunchTimeBefore(@Param("currentTime") Date currentTime);
}