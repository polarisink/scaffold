package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * udp服务器
 * <p>
 * 不能通过autowired的方式进行注入使用,只能通过eventBus进行沟通
 */
@Slf4j
@Component
//用于多实例部署
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class UdpVerticle extends VerticleBase {
    public static final String UDP_MSG_EVENT = "service.udp.event";
    private final NetProperties netProperties;
    private DatagramSocket socket;
    @Getter
    private volatile boolean running = false;

    private String getName() {
        return config().getString("serverName") + "-" + config().getInteger("instanceId", 0);
    }

    @Override
    public Future<?> start() {
        DatagramSocketOptions options = new DatagramSocketOptions()
                //端口和地址复用
                .setReuseAddress(true).setReusePort(true); // 必须开启，否则多实例部署会报错

        // 1. 创建 UDP Socket
        socket = vertx.createDatagramSocket(options);

        // 2. 设置数据包处理器
        socket.handler(packet -> {
            Buffer data = packet.data();

            log.info("{}收到来自 {} 的包: {}", getName(), packet.sender(), data.toString());
            // 回复数据
            socket.send("ACK", packet.sender().port(), packet.sender().host());
        });

        // 3. 绑定端口
        return socket.listen(netProperties.getUdpPort(), netProperties.getUdpHost())
                //成功之后的逻辑处理
                .onSuccess(s -> {
                    //打印成功日志
                    log.info("{}启动成功，端口：{}", getName(), s.localAddress().port());
                    //状态修改
                    running = true;
                    //启动成功之后事件监听，用于udp消息的发送
                    vertx.eventBus().<UdpMsgVo>consumer(UDP_MSG_EVENT, event -> {
                        UdpMsgVo vo = event.body();
                        socket.send(vo.buffer(), vo.port(), vo.host())
                                //成功
                                .onSuccess(v -> log.info("发送{}成功", vo.name()))
                                //失败
                                .onFailure(err -> log.error("发送{}失败: {}", vo.name(), err.getMessage()));
                    });
                }).onFailure(err -> log.error("{}启动失败： {}", getName(), err.getMessage()));
    }

    @Override
    public Future<?> stop() throws Exception {
        if (socket != null) {
            socket.close();
        }
        running = false;
        log.info("{}已停止", getName());
        return super.stop();
    }
}