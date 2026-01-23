package com.scaffold.vertx.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class VertxDeployer implements ApplicationRunner, DisposableBean {
    private final AtomicInteger ai = new AtomicInteger();
    private final ApplicationContext context;
    private final Vertx vertx;
    //利用 CountDownLatch 配合 Spring 的销毁钩子，可以让主线程在服务运行期间保持阻塞，而在收到停止指令时优雅释放。
    //private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void destroy() {
        if (vertx != null) {
            // 优雅关闭 Vert.x 并释放锁
            vertx.close().onComplete(ar -> {
                //latch.countDown();
                log.info("Vert.x 已安全关闭");
            });
        } else {
            //latch.countDown();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        // 注册自定义对象的解码器
        vertx.eventBus().registerDefaultCodec(UdpMsgVo.class, new GenericCodec<>(UdpMsgVo.class));

        //多实例部署
        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(4).setThreadingModel(ThreadingModel.VIRTUAL_THREAD);

        //tcp服务器部署
        Future<String> tcpFuture = vertx.deployVerticle(() -> {
                    TcpVerticle bean = context.getBean(TcpVerticle.class);
                    bean.setInstanceId(ai.getAndIncrement());
                    return bean;

                }, deploymentOptions)
                //成功
                .onSuccess(a -> log.info("tcp服务器部署成功"))
                //失败
                .onFailure(e -> log.error("部署失败：{}", e.getMessage()));

        //udp服务器部署
        Future<String> udpFuture = vertx.deployVerticle(() -> {
                    UdpVerticle bean = context.getBean(UdpVerticle.class);
                    bean.setInstanceId(ai.getAndIncrement());
                    return bean;
                }, deploymentOptions)
                //成功
                .onSuccess(a -> log.info("udp服务器部署成功"))
                //失败
                .onFailure(e -> log.error("udp服务器部署失败：{}", e.getMessage()));

        Future.all(tcpFuture, udpFuture)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        log.info("所有服务集群部署成功！系统运行中...");
                /*vertx.eventBus().send(UdpVerticle.UDP_MSG_EVENT, new JsonObject()
                        .put("data", new byte[]{1, 2, 3, 4})
                        .put("port", 8080)
                        .put("host", "127.0.0.1"));*/
                    } else {
                        log.error("部分服务部署失败，请检查配置", ar.cause());
                    }
                });

        //latch.await();

        // 关键：防止主线程执行完毕直接退出
        // 这会让主线程进入等待状态，直到进程被 kill 信号终止
        //Thread.currentThread().join();
    }
}