package com.scaffold.vertx.server;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainVerticle extends VerticleBase {
    @Override
    public Future<?> start() {
        ConfigStoreOptions yamlStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml") // 必须指定格式为 yaml
                .setConfig(new JsonObject().put("path", "config.yaml"));
        // 1. 初始化配置读取器
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(yamlStore));

        // 2. 获取配置并执行部署
        return retriever.getConfig().compose(c -> {
            // 1. 注册编解码器
            vertx.eventBus().registerDefaultCodec(UdpMsgVo.class, new GenericCodec<>(UdpMsgVo.class));

            // 2. 定义需要启动的服务列表
            // 格式：Verticle类, 实例数, 基础名称
            List<ServiceConfig> services = List.of(
                    new ServiceConfig(HttpVerticle::new, 4, "http-server", c.getInteger("httpPort"), c.getString("httpHost")),
                    new ServiceConfig(TcpVerticle::new, 4, "tcp-server", c.getInteger("tcpPort"), c.getString("tcpHost")),
                    new ServiceConfig(UdpVerticle::new, 4, "udp-server", c.getInteger("udpPort"), c.getString("udpHost")),
                    new ServiceConfig(WebSocketVerticle::new, 4, "ws-server", c.getInteger("wsPort"), c.getString("wsHost"))
            );
            // 3. 执行统一并行部署
            List<Future<String>> allFutures = services.stream().map(this::deployService).toList();
            return Future.all(allFutures).onComplete(ar -> {
                if (ar.succeeded()) {
                    log.info("🚀 所有服务集群部署成功！系统运行中...");
                } else {
                    log.error("❌ 部分服务部署失败", ar.cause());
                }
            });
        }).onFailure(e -> log.error("无法加载配置文件", e));
    }

    @Override
    public Future<?> stop() throws Exception {
        return super.stop();
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
                    .setConfig(new JsonObject().put("instanceId", i).put("serverName", cfg.name).put("port", cfg.port).put("host", cfg.host));

            // 执行部署
            instanceFutures.add(vertx.deployVerticle(cfg.supplier.get(), opt));
        }
        return Future.all(instanceFutures).map(cfg.name + " OK");
    }
}
