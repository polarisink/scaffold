package com.scaffold.vertx.server;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class VertxDeployer implements ApplicationRunner, DisposableBean {
    private final ApplicationContext context;
    private final Vertx vertx;
    // 利用 CountDownLatch 配合 Spring 的销毁钩子，可以让主线程在服务运行期间保持阻塞，而在收到停止指令时优雅释放。
    // private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void destroy() {
        if (vertx != null) {
            // 优雅关闭 Vert.x 并释放锁
            vertx.close().onComplete(ar -> {
                // latch.countDown();
                log.info("Vert.x 已安全关闭");
            });
        } /*else {
            latch.countDown();
        }*/
        log.info("vertx服务器已停止");
    }

    @Override
    public void run(ApplicationArguments args) {
        // 1. 注册编解码器
        vertx.eventBus().registerDefaultCodec(UdpMsgVo.class, new GenericCodec<>(UdpMsgVo.class));

        // 2. 定义需要启动的服务列表
        // 格式：Verticle类, 实例数, 基础名称
        List<ServiceConfig> services = List.of(
                new ServiceConfig(HttpVerticle.class, 4, "http-server"),
                new ServiceConfig(TcpVerticle.class, 4, "tcp-server"),
                new ServiceConfig(UdpVerticle.class, 4, "udp-server"),
                new ServiceConfig(WebSocketVerticle.class, 4, "ws-server")
        );

        // 3. 执行统一并行部署
        List<Future<String>> allFutures = services.stream().map(this::deployService).toList();

        Future.all(allFutures).onComplete(ar -> {
            if (ar.succeeded()) {
                log.info("🚀 所有服务集群部署成功！系统运行中...");
            } else {
                log.error("❌ 部分服务部署失败", ar.cause());
            }
        });
        // latch.await();

        // 关键：防止主线程执行完毕直接退出
        // 这会让主线程进入等待状态，直到进程被 kill 信号终止
        // Thread.currentThread().join();
    }

    /**
     * 通用的部署逻辑：循环逐个部署，确保 ID 分配和端口复用安全
     */
    private Future<String> deployService(ServiceConfig cfg) {
        List<Future<String>> instanceFutures = new ArrayList<>();

        for (int i = 0; i < cfg.instances; i++) {
            // 每一个实例都拥有独立的 DeploymentOptions 和 Config
            DeploymentOptions opt = new DeploymentOptions()
                    // 虚拟线程开启
                    .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                    // 设置实例id
                    .setConfig(new JsonObject().put("instanceId", i).put("serverName", cfg.name));

            // 执行部署
            instanceFutures.add(vertx.deployVerticle(() -> context.getBean(cfg.clazz), opt));
        }
        return Future.all(instanceFutures).map(cfg.name + " OK");
    }

    @Data
    @AllArgsConstructor
    private static class ServiceConfig {
        Class<? extends VerticleBase> clazz;
        int instances;
        String name;
    }
}