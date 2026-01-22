package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TcpVerticle extends VerticleBase {
    private NetServer server;
    @Setter
    private Integer instanceId;

    private String getName() {
        return "tcp服务器-" + instanceId;
    }

    @Override
    public Future<?> start() {
        //用于多实例共享map
        Map<String, String> map = vertx.sharedData().getLocalMap("tcp");

        // 1. 创建服务器
        server = vertx.createNetServer(new NetServerOptions().setReusePort(true));

        // 2. 配置连接处理器
        server.connectHandler(socket -> {
            String remoteAddress = socket.remoteAddress().toString();
            // 处理器接收到的数据
            socket.handler(buffer -> {
                log.info("{}从：{}收到数据: {}", getName(), remoteAddress, buffer.toString());
                socket.write("PONG: " + buffer);
            });

            // 监听关闭事件
            socket.closeHandler(v -> log.info("连接已关闭: {}", remoteAddress));
        });

        // 3. 启动监听（5.x 推荐 Future 风格）
        return server.listen(1234).onSuccess(s -> log.info("{}启动成功，端口： {}", getName(), s.actualPort())).onFailure(err -> log.error("{}启动失败： {}", getName(), err.getMessage()));
    }

    @Override
    public Future<?> stop() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        log.info("{}已停止", getName());
        return super.stop();
    }
}