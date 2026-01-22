package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * udp服务器
 * <p>
 * 不能通过autowired的方式进行注入使用
 */
@Slf4j
@Component
//用于多实例部署
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UdpVerticle extends VerticleBase {
    public static final String UDP_MSG_EVENT = "service.udp.event";
    private DatagramSocket socket;
    @Getter
    private volatile boolean running = false;
    @Setter
    @Getter
    private Integer instanceId;

    private String getName() {
        return "udp服务器-" + instanceId;
    }

    @Override
    public Future<?> start() {
        DatagramSocketOptions options = new DatagramSocketOptions()
                .setReuseAddress(true)
                .setReusePort(true); // 必须开启，否则多实例部署会报错

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
        return socket.listen(8079, "0.0.0.0")
                .onSuccess(s -> {
                    log.info("{}启动成功，端口：{}", getName(), s.localAddress().port());
                    running = true;
                    //启动成功之后事件监听，用于udp消息的发送
                    vertx.eventBus().consumer(UDP_MSG_EVENT, message -> {
                        JsonObject body = (JsonObject) message.body();
                        // 调用内部的发送方法
                        socket.send(Buffer.buffer(body.getBinary("data")), body.getInteger("port"), body.getString("host"))
                                //成功
                                .onSuccess(v -> log.info("发送成功"))
                                //失败
                                .onFailure(err -> log.error("发送失败: {}", err.getMessage()));
                    });
                }).onFailure(err -> log.error("{}启动失败： {}", getName(), err.getMessage()));
    }

    @Override
    public Future<?> stop() throws Exception {
        // 确保 instanceId 存在
        if (this.instanceId == null) {
            this.instanceId = (int) (System.currentTimeMillis() % 100000);
        }
        if (socket != null) {
            socket.close();
        }
        running = false;
        log.info("{}已停止", getName());
        return super.stop();
    }
}