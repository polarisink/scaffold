# scaffold-socketio-starter

基于 `netty-socketio` 的 Spring Boot Starter。引入依赖后会自动创建并启动
`SocketIOServer`，并在 Spring 容器关闭时停止服务。

## 主要能力

- `WebSocketAutoConfiguration`：Socket.IO 服务端自动配置。
- `WebSocketProperties`：类型安全的 `scaffold.socketio` 配置。
- `AuthorizationListener`：默认放行的连接认证实现，可由业务 Bean 覆盖。
- `WsManager`：连接管理与消息推送辅助方法。

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-socketio-starter</artifactId>
</dependency>
```

```yaml
scaffold:
  socketio:
    enabled: true
    host: 127.0.0.1
    port: 8081
    context: ""
    transports: websocket,polling
```

`enabled` 默认为 `true`。若要自定义连接认证，在应用中声明一个
`AuthorizationListener` Bean 即可替换默认实现。完整示例见
`scaffold-test/scaffold-test-socketio`。

## Native Image

Starter 已内置 netty-socketio 协议层所需的 Native Image 支持：

- 显式创建 `JacksonJsonSupport`，避免运行时反射加载实现类。
- 注册 `AuthPacket` 和 `ConnPacket` 的反射信息，确保 Engine.IO 握手包及
  Socket.IO 连接确认包能够正确序列化 `sid`、心跳间隔等字段。
- 上述配置由 `WebSocketAutoConfiguration` 自动导入，业务应用不需要重复声明。

### 推荐：编程式注册监听器

Native 应用推荐直接调用 `SocketIOServer` API。方法引用和 Lambda 会进入 Native Image
的静态可达调用图，业务监听方法不需要额外维护 RuntimeHints：

```java
@Component
@Lazy(false)
public class SocketIoHandler {

    public SocketIoHandler(SocketIOServer server) {
        server.addConnectListener(this::onConnect);
        server.addDisconnectListener(this::onDisconnect);
        server.addEventListener("message", String.class, this::onMessage);
    }

    private void onConnect(SocketIOClient client) {
        // 建立连接
    }

    private void onDisconnect(SocketIOClient client) {
        // 断开连接
    }

    private void onMessage(SocketIOClient client, String message, AckRequest ack) {
        client.sendEvent("message", message);
        if (ack.isAckRequested()) {
            ack.sendAckData("ok");
        }
    }
}
```

Spring 默认会在启动阶段创建非懒加载单例，因此构造函数会及时注册监听器。示例中的
`@Lazy(false)` 用于保证应用即使开启了 `spring.main.lazy-initialization=true`，监听器仍会
在启动时注册。

完整的 Native 测试实现见
[scaffold-test-native](../../scaffold-test/scaffold-test-native/src/main/java/com/scaffold/nativetest/SocketIoNativeTestHandler.java)。

### 注解监听器

`@OnConnect`、`@OnDisconnect` 和 `@OnEvent` 由 `SpringAnnotationScanner` 通过反射发现并
调用。普通 JVM 应用可以继续使用；Native Image 应用如果使用这些注解，必须为业务监听类
注册方法反射信息：

```java
public class SocketIoHandlerRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(
                SocketIoHandler.class,
                MemberCategory.INVOKE_DECLARED_METHODS);
    }
}
```

然后在应用配置类上导入：

```java
@ImportRuntimeHints(SocketIoHandlerRuntimeHints.class)
@SpringBootApplication
public class Application {
}
```

当监听器较多时，需要持续维护这些业务类的提示信息，因此 Native 应用更推荐编程式注册。

### 自定义事件 DTO

编程式注册可以消除监听方法的反射调用，但 Jackson 在 Native Image 中序列化或反序列化
自定义 DTO 时仍然需要类型元数据。可以在配置类上注册：

```java
@RegisterReflectionForBinding(ChatMessage.class)
@Configuration(proxyBeanMethods = false)
public class SocketIoNativeConfiguration {
}
```

对应监听器可以使用明确的事件类型：

```java
server.addEventListener("chat", ChatMessage.class, this::onChatMessage);
```

也可以使用一个类统一配置
```java
public class SocketIoMessageRuntimeHints implements RuntimeHintsRegistrar {

    private static final Class<?>[] MESSAGE_TYPES = {
            ChatMessage.class,
            LoginMessage.class,
            NotificationMessage.class
    };

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (Class<?> messageType : MESSAGE_TYPES) {
            hints.reflection().registerType(
                    messageType,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                    MemberCategory.DECLARED_FIELDS
            );
        }
    }
}
```

然后导入
```java
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(SocketIoMessageRuntimeHints.class)
public class SocketIoNativeConfiguration {
}
```

如果事件载荷使用 `String`、`Map` 或 Jackson `JsonNode`，通常不需要为业务 DTO 增加反射配置。

### 构建与验证

在项目根目录执行：

```bash
./mvnw -Pnative -pl scaffold-test/scaffold-test-native -am \
  -Dmaven.test.skip=true clean package
```

运行生成的二进制后访问测试页面：

```text
http://localhost:8082/index.html
```

确认页面能够完成以下流程：

- Socket.IO 连接后保持在线，而不是反复连接和断开。
- 收到 `server-ready` 事件和健康检查 ACK。
- `native-echo` 事件及 ACK 均能正常返回。

若浏览器不断重连，应首先检查浏览器控制台和握手帧。连接确认包必须包含 `sid`，例如：

```text
40{"sid":"..."}
```

## Netty 版本兼容性

`netty-socketio 2.0.14` 基于 Netty 4.1 构建。当前 `scaffold-dependencies` 将 Netty
与 Spring Boot 4.1.1 的依赖基线统一为 `4.2.17.Final`，这会同时影响所有使用 Netty 的应用。

后续升级或排查 Spring Cloud Gateway、WebFlux/Reactor Netty、Vert.x、Dubbo、
Redisson 等组件时，需要优先检查该全局约束。长期建议是在 Socket.IO 服务端支持
Spring Boot 当前 Netty 主版本后统一升级，或将 Netty 4.1 覆盖移入 Socket.IO 专用应用/BOM，
避免影响其他 Spring 应用。
