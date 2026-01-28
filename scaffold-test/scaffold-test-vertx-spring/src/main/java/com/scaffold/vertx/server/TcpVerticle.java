package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * tcp服务器
 * <p>
 * 不能通过autowired的方式进行注入使用,只能通过eventBus进行沟通
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class TcpVerticle extends VerticleBase {
    private final NetProperties netProperties;
    private volatile boolean running = false;
    private NetServer server;

    private String getName() {
        return config().getString("serverName") + "-" + config().getInteger("instanceId", 0);
    }

    @Override
    public Future<?> start() {
        //用于多实例共享map
        Map<String, String> map = vertx.sharedData().getLocalMap("tcp");
        // 1. 创建服务器
        server = vertx.createNetServer(new NetServerOptions().setReusePort(true));

        // 2. 配置连接处理器
        server.connectHandler(socket -> {
            // 1. 初始化解析器，先设置为读取 4 字节（头部长度）
            RecordParser parser = RecordParser.newFixed(4);

            parser.handler(new Handler<>() {
                int payloadLength = -1;

                @Override
                public void handle(Buffer buffer) {
                    if (payloadLength == -1) {
                        // A. 当前正在读取 Header (4 字节)
                        payloadLength = buffer.getInt(0);
                        log.info("即将接收 Body，长度为: {}", payloadLength);

                        // 动态切换解析器：下次要读取 payloadLength 长度的 Body
                        parser.fixedSizeMode(payloadLength);
                    } else {
                        // B. 当前正在读取 Body
                        log.info("收到完整数据包体: {}", buffer.toString());

                        // 重置状态：下次继续读取 4 字节的 Header
                        payloadLength = -1;
                        parser.fixedSizeMode(4);
                    }
                }
            });

            socket.handler(parser);
        });

        // 3. 启动监听（5.x 推荐 Future 风格）
        return server.listen(netProperties.getTcpPort(), netProperties.getTcpHost())
                //成功
                .onSuccess(s -> {
                    log.info("{}启动成功，端口： {}", getName(), s.actualPort());
                    running = true;
                })
                //失败
                .onFailure(err -> log.error("{}启动失败： {}", getName(), err.getMessage()));
    }

    @Override
    public Future<?> stop() throws Exception {
        if (server != null && running) {
            server.shutdown();
        }
        log.info("{}已停止", getName());
        return super.stop();
    }
}