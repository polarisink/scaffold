package com.github.polarisink.download.scheduled;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TrainEngineServiceTest {
    public static void main(String[] args) throws InterruptedException {
        TrainEngineService service = new TrainEngineService();
        String trainId = "111";
        service.initScheduler(trainId, LocalDateTime.now());
        service.step(trainId, 3);
        TimeUnit.SECONDS.sleep(10);
    }
}
