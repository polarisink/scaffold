# Netty UDP 与 JNA/PDW 示例

模块包含两部分：Netty UDP 服务端基础设施，以及通过 JNA 调用 `Pdw_lib` 动态库解析 PDW/信号数据的结构映射。

```bash
./mvnw -pl scaffold-test/scaffold-test-udp -am -Pexamples spring-boot:run
```

Spring Boot 管理端口默认为 `8082`。UDP 监听参数由 `UdpProperties` 的 `udp.*` 配置绑定，使用前应结合源码补齐本机监听地址、端口和线程参数。

仓库只附带 Windows `Pdw_lib.dll` 和 C 头文件；macOS/Linux 无法直接加载该 DLL，需要获得对应平台、CPU 架构匹配的 `.dylib`/`.so`。`pdw.parse` 配置控制最小脉宽与脉冲间隔范围，动态库不在系统搜索路径时需设置库路径。原生结构的字段顺序、对齐和位宽必须与 `Pdw_lib.h` 保持一致，否则可能产生错误数据或进程崩溃。
