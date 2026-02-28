package com.scaffold.vertx.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * HTTP服务器
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class HttpVerticle extends VerticleBase {
    private final NetProperties netProperties;
    private HttpServer server;

    private String getName() {
        return config().getString("serverName") + "-" + config().getInteger("instanceId", 0);
    }

    @Override
    public Future<?> start() throws Exception {
        HttpServerOptions options = new HttpServerOptions();
        options.setReuseAddress(true);
        options.setReusePort(true);

        // 1、创建http服务器
        server = vertx.createHttpServer(options);

        // 2、配置router
        Router router = Router.router(vertx);
        // 处理静态资源（如 HTML/JS）
        router.route("/static/*").handler(StaticHandler.create());

        // GET 接口
        router.get("/api/user/:id").handler(ctx -> {
            String userId = ctx.pathParam("id");
            ctx.response().end("Querying user: " + userId);
        });

        // 发送udp消息
        router.get("/udp").handler(ctx -> {
            UdpMsgVo udpMsgVo = UdpMsgVo.builder().name("test").host("127.0.0.1").port(8080).buffer(Buffer.buffer("hello udp")).build();
            // 这种方式天然保证：如果 Verticle 没启动成功（没注册消费者），消息会自动堆积或报错
            // 且 Vert.x 会自动在多个实例间进行负载均衡
            vertx.eventBus().send(UdpVerticle.UDP_MSG_EVENT, udpMsgVo);
            ctx.response().end();
        });

        //ws踢人下线
        router.delete("/ws/kick/:id").handler(ctx -> {
            String userId = ctx.pathParam("id");
            vertx.eventBus().publish("kick.user.all_instances", userId);
        });

        // POST 接口（需要 BodyHandler 解析请求体）
        router.route().handler(BodyHandler.create());
        router.post("/api/save").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            ctx.response().setStatusCode(201).putHeader("content-type", "application/json").end(new JsonObject().put("status", "saved").encode());
        });

        // 3、运行server
        return server
                //使用router
                .requestHandler(router)
                //监听
                .listen(netProperties.getHttpPort(), netProperties.getHttpHost())
                //成功
                .onSuccess(s -> log.info("{}启动成功，端口： {}", getName(), s.actualPort()))
                //失败
                .onFailure(err -> log.error("{}启动失败", getName(), err));
    }

    @Override
    public Future<?> stop() throws Exception {
        log.info("{}已停止", getName());
        return server != null ? server.close() : Future.succeededFuture();
    }
}
