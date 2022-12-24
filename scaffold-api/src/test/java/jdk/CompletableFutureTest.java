package jdk;

import cn.hutool.core.util.IdUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.util.StopWatch;

/**
 * @author aries
 * @date 2022/9/21
 */
public class CompletableFutureTest {

  @Test
  public void test() throws ExecutionException, InterruptedException {
    List<Long> list = new ArrayList<>();
    StopWatch watch = new StopWatch();
    watch.start();
    CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      list.add(IdUtil.getSnowflakeNextId());
    });
    CompletableFuture<Void> async1 = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      list.add(IdUtil.getSnowflakeNextId());
    });
    CompletableFuture<Void> async2 = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      list.add(IdUtil.getSnowflakeNextId());
    });
    CompletableFuture<Void> async3 = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      list.add(IdUtil.getSnowflakeNextId());
    });
    CompletableFuture.allOf(async, async1, async2, async3).get();
    watch.stop();
    System.out.println(list);
    System.out.println(watch.getLastTaskTimeMillis());
  }
}
