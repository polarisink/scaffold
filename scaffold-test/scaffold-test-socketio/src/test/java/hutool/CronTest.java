package hutool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CronTest {
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        schedule();
        schedule();
    }

    @SneakyThrows
    public static void schedule() {
        AtomicInteger c = new AtomicInteger();
        executor.scheduleAtFixedRate(() -> {
            if (c.getAndIncrement() < 10) {
                log.info("scheduleAtFixedRate:{}", LocalDateTime.now());
            } else {
                executor.shutdown();
            }

        }, 1, 1, TimeUnit.SECONDS);
    }


}
