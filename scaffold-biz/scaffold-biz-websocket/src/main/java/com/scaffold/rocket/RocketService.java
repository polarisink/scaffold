
package com.scaffold.rocket;

import com.scaffold.socket.util.NettySocketUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 火箭发射引擎demo
 * 部分数据暂时放在内存map中，后续优化更新
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RocketService {
    // 初始仿真速度为1倍速
    private static final double INITIAL_SPEED_MULTIPLIER = 1.0;
    //步进
    private final Map<String, Double> steps = new ConcurrentHashMap<>();
    //训练是否暂停map
    private final Map<String, Boolean> pauses = new ConcurrentHashMap<>();
    //任务执行future，双层map，不同训练的彼此隔离开
    private final Map<String, Map<String, ScheduledFuture<?>>> schedulers = new ConcurrentHashMap<>();

    // 数据库mapper
    private final RocketRepository rocketRepository;
    // 共享的线程池
    private final ScheduledExecutorService sharedScheduler;
    private final RocketCache rocketCache;

    /**
     * 火箭数据的业务代码
     *
     * @param rocket 火箭
     */
    private void sendRocketData(Rocket rocket) {
        if (rocket == null) {
            return;
        }
        //todo  当火箭到达目的地就停止
        String trainId = "";
        //训练暂停
        if (pauses.computeIfAbsent(trainId, t -> false)) {
            log.info("训练暂停：{}", trainId);
            return;
        }
        //发送数据
        //todo 具体的有经纬高俯仰角等参数需要计算
        String rocketId = rocket.getId();
        double newHeight = rocketCache.updateHeight(rocketId, rocketCache.getCurrentHeight(rocketId));
        NettySocketUtil.sendNotice(newHeight);
    }

    /**
     * 选择指定火箭发射
     *
     * @param rocketId 火箭id
     */
    public void launchRocket(String rocketId) {
        Optional<Rocket> optionalRocket = rocketRepository.findById(rocketId);
        if (!optionalRocket.isPresent()) {
            throw new IllegalArgumentException("Rocket not found with ID: " + rocketId);
        }
        Rocket rocket = optionalRocket.get();
        String trainId = rocket.getTrainId();
        // 从map中取出这个训练的步进计算时间间隔进行数据发送，scheduler保存到map中
        Double v = steps.computeIfAbsent(trainId, t -> INITIAL_SPEED_MULTIPLIER);
        ScheduledFuture<?> scheduledFuture = sharedScheduler.scheduleAtFixedRate(() -> sendRocketData(rocket), 0, calculatePeriod(v), TimeUnit.MILLISECONDS);
        schedulers.computeIfAbsent(trainId, t -> new ConcurrentHashMap<>()).put(rocket.getId(), scheduledFuture);
    }

    /**
     * 修改训练状态
     *
     * @param trainId 训练id
     * @param status
     */
    public void runningStatus(String trainId, boolean status) {
        //不仅仅暂停火箭，还需要其他等设备
        pauses.put(trainId, status);
        Thread.currentThread().interrupt();
    }

    /**
     * 设置训练步进
     *
     * @param trainId 训练id
     * @param step    步进
     */
    public void updateStep(String trainId, double step) {
        steps.put(trainId, step);
        Map<String, ScheduledFuture<?>> serviceMap = schedulers.getOrDefault(trainId, Map.of());
        // 找到这个训练下所有任务，并关闭，设置新的任务
        int newPeriod = calculatePeriod(step);
        serviceMap.forEach((rocketId, scheduler) -> {
            if (!scheduler.isCancelled()) {
                //没停止的任务直接停止，使用新任务替代，停止的就不要管了
                scheduler.cancel(false);
                Optional<Rocket> optionalRocket = rocketRepository.findById(rocketId);
                if (optionalRocket.isPresent()) {
                    Rocket rocket = optionalRocket.get();
                    ScheduledFuture<?> scheduledFuture = sharedScheduler.scheduleAtFixedRate(() -> sendRocketData(rocket), 0, newPeriod, TimeUnit.MILLISECONDS);
                    serviceMap.put(rocketId, scheduledFuture);
                }
            }
        });
    }


    /**
     * 计算时间片
     *
     * @param step 步进
     * @return 时间片
     */
    private static int calculatePeriod(double step) {
        BigDecimal period = BigDecimal.valueOf(1000).divide(BigDecimal.valueOf(step), RoundingMode.HALF_UP);
        return period.intValue();
    }
}