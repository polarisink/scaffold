package com.scaffold.vertx;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1. 发送消息的方法
 * send(address, message)
 * 模式： 点对点 (Point-to-Point)
 * 作用： 将消息发送到指定的地址。如果有多个消费者监听该地址，Vert.x 会使用 轮询算法 (Round-robin) 选择其中一个进行交付。
 * 场景： 任务分发，确保一个任务只被一个 Worker 处理。
 * <p>
 * publish(address, message)
 * 模式： 发布/订阅 (Publish/Subscribe)
 * 作用： 将消息广播到指定地址。所有注册在该地址上的订阅者都会收到这条消息。
 * 场景： 状态更新通知、配置变更广播。
 * <p>
 * request(address, message, handler)
 * 模式： 请求/响应 (Request-Response)
 * 作用： 发送消息并期待一个回调。它是异步的，接收方通过 reply() 方法返回数据。
 * 场景： 查询数据库、调用另一个服务的接口并获取结果。
 * <p>
 * 2. 接收与监听方法
 * consumer(address, handler)
 * 作用： 注册一个监听器来接收消息。返回一个 MessageConsumer 对象。
 * 特点： handler 会处理收到的 Message 对象，你可以通过 message.body() 获取内容，通过 message.reply() 回复。
 * localConsumer(address, handler)
 * 作用： 与 consumer 类似，但该监听器仅限本地集群节点可见。
 * 场景： 当你不希望消息穿过网络到达集群中的其他机器时使用，提高性能和安全性。
 * <p>
 * 方法,作用
 * handler(handler),        修改或设置处理逻辑。
 * pause() / resume(),      暂停或恢复接收消息（背压控制）。
 * unregister(),            注销监听器，停止接收消息。
 * completionHandler(res),  监听注册是否成功的通知。
 * <p>
 * 特性        send         publish        request
 * 接收者数量   1个 (轮询)    所有订阅者       1个 (轮询)
 * 是否有反馈   无           无              有 (Future/Handler)
 * 典型用途     任务队列      事件通知         远程过程调用 (RPC)
 * <p>
 * 如果你是在分布式环境下使用，记得配置 ClusterManager（如 Hazelcast 或 Ignite），这样这些方法就能跨 JVM 工作了
 */
@SpringBootApplication
@RequiredArgsConstructor
public class SpringVertxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringVertxApplication.class, args);
    }
}
