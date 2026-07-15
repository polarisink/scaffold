# SPI 测试使用

本模块用四组独立示例说明 Java SPI 在支付插件、日志门面、文件转换器和数据库驱动中的应用。实现均为演示代码，不会连接真实支付平台、日志框架或数据库。

先编译，再在 IDE 中分别运行以下 `main`：

- `PaymentPluginSystem`
- `LoggingFacadeSystem`
- `FileConverterSystem`
- `DatabaseDriverSystem`

```bash
./mvnw -pl scaffold-test/scaffold-test-spi -am -Pexamples package
```

`META-INF/services/com.scaffold.spi.PaymentService` 展示标准 JDK SPI 注册文件；其他示例侧重展示插件系统的结构。嵌套实现类若要通过 `ServiceLoader` 加载，服务文件中必须使用其二进制类名（包含 `$`）。

## 使用注意事项

- 第一，配置文件位置。SPI配置文件必须放在META-INF/services目录下，文件名必须是接口的全限定名。配置文件内容为实现类的全限定名，每行一个实现类。

- 第二，无参构造函数。所有服务实现类必须提供公共的无参构造函数，否则ServiceLoader无法实例化。如果需要初始化参数，可以在实例化后通过setter方法设置。

- 第三，类加载器问题。在某些环境下（如OSGi、Web容器），需要使用正确的类加载器。可以通过load方法的第二个参数指定自定义类加载器。

- 第四，性能考虑。ServiceLoader是懒加载机制，只有在迭代时才会实例化服务。如果服务实现类的构造函数很重，可能影响性能。对于频繁使用的场景，建议缓存已加载的服务实例。

- 第五，异常处理。如果某个服务实现类实例化失败，整个加载过程会中断。建议使用loadFirstAvailable方法，它会跳过实例化失败的实现，继续尝试下一个。

- 第六，单例问题。ServiceLoader每次迭代都会创建新的实例，不是单例模式。如果需要单例，应该自己管理实例的生命周期。

- 第七，优先级控制。Java SPI本身不支持优先级，如果需要按优先级选择服务，需要在接口中定义优先级方法，加载后自行排序选择。
