package com.scaffold.rocket;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Rocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private double latitude; // 纬度
    private double longitude; // 经度
    private double speed = 0.1; // 每秒移动的距离（单位：度）
    private double heading = 45.0; // 方向（单位：度）
    private boolean launched = false; // 是否已经发射
    private Date scheduledLaunchTime; // 预定发射时间

    public String trainId;

    public double initialHeight;
}