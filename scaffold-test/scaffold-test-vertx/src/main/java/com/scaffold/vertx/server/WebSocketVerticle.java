package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ws服务器
 * <p>
 * 特性        协议级心跳 (Ping/Pong Frame)            业务级心跳 (Text/Binary Message)
 * 所属层级     传输层 (WebSocket Protocol)            应用层 (Business Logic)
 * 数据内容     通常为空或少量字节                       可携带复杂的 JSON 或状态信息
 * 处理器触发   frameHandler                          textMessageHandler
 * 浏览器支持   浏览器自动回复 Pong，无法手动触发 Ping     必须由开发者手动写 JS 发送
 * 检测目的     检测 TCP/WebSocket 链路是否断开          检测应用是否活着、用户是否在线
 * 性能消耗     极低                                   略高（需要经过字符串/JSON 解析）
 * <p>
 * 为什么通常协议心跳和业务心跳两者都要做？
 * 浏览器限制：大多数浏览器不允许 JavaScript 手动发送标准的 0x9 Ping 帧。因此，客户端发往服务端的心跳通常必须是“业务级”的。
 * 服务端检查：服务端向客户端发送 Ping 帧（协议级）可以有效检测僵尸连接。
 * 穿透代理：有些负载均衡器（Load Balancer）只在有“数据往来”时才保持连接。协议帧有时被视为管理流量，不计入活跃数据，而业务字符串消息则一定能延长连接寿命。
 *
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class WebSocketVerticle extends VerticleBase {
    public static final String WS_BROADCAST_ALL = "broadcast.all";
    public static final String WS_INSTANCE = "ws.instance.";
    public static final String WS_KICK = "ws.command.kick";
    // 1. 本地连接池：Key=userId, Value=Socket对象 (仅限连接到本实例的用户)
    private final Map<String, ServerWebSocket> localSocketMap = new ConcurrentHashMap<>();
    // 2. 本地活跃时间池：Key=userId, Value=最后一次Ping/消息时间戳
    private final Map<String, Long> lastActiveTimeMap = new ConcurrentHashMap<>();
    private final List<MessageConsumer<?>> consumerList = new ArrayList<>();
    private final NetProperties netProperties;
    // 3. 心跳定时器id
    private Long heartbeatTimerId;
    // 4. ws服务器
    private HttpServer server;

    private Integer getInstanceId() {
        return config().getInteger("instanceId", 0);
    }

    /**
     * 获取全局共享映射 (Key=userId, Value=instanceId)
     * 用于跨实例查找用户所在的服务器节点
     */
    private LocalMap<String, Integer> getUserLocationMap() {
        return vertx.sharedData().getLocalMap("global.user.locations");
    }

    /**
     * 服务器名字
     *
     * @return
     */
    private String getName() {
        return config().getString("serverName") + "-" + config().getInteger("instanceId", 0);
    }

    @Override
    public Future<?> start() {

        // 1. 创建服务器实例（此时还未开始监听）
        server = vertx.createHttpServer(new HttpServerOptions()
                //端口复用
                .setReusePort(true).setReuseAddress(true)
                // 传输层超时设长一点，业务层自己管心跳
                .setIdleTimeout(120));

        // 2. 配置 WebSocket 业务逻辑处理器
        setupWebSocketHandler();

        // 3. 执行端口绑定，并利用回调开启后续任务
        return server.listen(netProperties.getWsPort(), netProperties.getWsHost())
                //成功回调
                .onSuccess(s -> {

                    // --- 关键：只有成功后才执行以下初始化 ---

                    // 注册 EventBus 消费者（全服公告、定向消息、强踢指令）
                    registerEventBusConsumers();

                    // 启动心跳检测定时器
                    startHeartbeatTimer();

                    //打印成功日志
                    log.info("{} 启动成功，端口： {}", getName(), server.actualPort());
                })
                //失败打印原因
                .onFailure(e -> log.error("{} 启动失败", getName(), e));
    }

    /**
     * 配置 WebSocket 握手及消息处理
     */
    private void setupWebSocketHandler() {
        server.webSocketHandler(ws -> {
            String userId = ws.query().split("=")[1]; // 假设 URL 是 /chat?user=123
            if (userId == null) {
                //todo 如何退出
                return;
            }

            // 处理上线逻辑
            handleUserOnline(userId, ws);

            //【协议级心跳维护】: 监听底层 Ping/Pong 帧
            ws.frameHandler(frame -> {
                if (frame.isPing() || frame.isPing()) {
                    // 只要有底层心跳交互，就认为用户活跃
                    lastActiveTimeMap.put(userId, System.currentTimeMillis());
                    log.debug("收到来自用户 {} 的标准 Ping 帧", userId);
                    // 注意：Vert.x 会自动回复 Pong，通常不需要你手动写 writePong
                }
            });
            // 处理文本消息
            ws.textMessageHandler(msg -> {

                // 4. 【业务级心跳维护】: 监听文本消息
                lastActiveTimeMap.put(userId, System.currentTimeMillis());
                if ("PING".equalsIgnoreCase(msg)) {
                    ws.writeTextMessage("PONG");
                } else {
                    log.info("{} 收到消息: {}", userId, msg);
                }
            });
            // 处理下线
            ws.closeHandler(v -> handleUserOffline(userId));
            ws.exceptionHandler(e -> ws.close());
        });
    }

    /**
     * 注册所有跨实例通信监听
     */
    private void registerEventBusConsumers() {
        // 全服公告
        consumerList.add(vertx.eventBus().<String>consumer(WS_BROADCAST_ALL, msg -> {
            localSocketMap.values().forEach(ws -> ws.writeTextMessage("【系统】" + msg.body()));
        }));

        // 定向消息 (ws.instance.0, ws.instance.1 ...)
        consumerList.add(vertx.eventBus().<JsonObject>consumer(WS_INSTANCE + getInstanceId(), msg -> {
            JsonObject json = msg.body();
            ServerWebSocket ws = localSocketMap.get(json.getString("to"));
            if (ws != null) ws.writeTextMessage(json.getString("payload"));
        }));
        // 强踢指令
        consumerList.add(vertx.eventBus().<String>consumer(WS_KICK, msg -> {
            String uid = msg.body();
            ServerWebSocket ws = localSocketMap.get(uid);
            if (ws != null) {
                ws.writeTextMessage("你已被系统强制下线").onComplete(a -> ws.close());
                handleUserOffline(uid);
            }
        }));
    }

    /**
     * 启动心跳扫描定时器
     */
    private void startHeartbeatTimer() {
        this.heartbeatTimerId = vertx.setPeriodic(30000, id -> {
            long now = System.currentTimeMillis();
            lastActiveTimeMap.forEach((uid, lastTime) -> {
                ServerWebSocket ws = localSocketMap.get(uid);
                if (now - lastTime > 90000) {
                    log.warn("{} 用户 {} 心跳超时", getName(), uid);
                    if (ws != null) ws.close();
                    handleUserOffline(uid);
                }
            });
        });
    }

    /**
     * 处理用户上线
     *
     * @param userId 用户id
     * @param ws     ws客户端
     */
    private void handleUserOnline(String userId, ServerWebSocket ws) {
        localSocketMap.put(userId, ws);
        lastActiveTimeMap.put(userId, System.currentTimeMillis());
        getUserLocationMap().put(userId, getInstanceId());
    }

    /**
     * 处理用户下线
     *
     * @param userId 用户id
     */
    private void handleUserOffline(String userId) {
        localSocketMap.remove(userId);
        lastActiveTimeMap.remove(userId);
        getUserLocationMap().removeIfPresent(userId, getInstanceId());
    }

    @Override
    public Future<?> stop() throws Exception {
        //停止定时器
        if (heartbeatTimerId != null) {
            vertx.cancelTimer(heartbeatTimerId);
        }
        //停止所有消费者
        consumerList.forEach(MessageConsumer::unregister);
        return server != null ? server.close() : Future.succeededFuture();
    }
}