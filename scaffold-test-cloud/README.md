# 云服务测试示例

该模块包含 Spring Cloud、Spring Cloud Gateway、Nacos 和 Dubbo 的独立示例。

## 子模块

| 模块 | 用途 | 端口 |
| --- | --- | --- |
| `scaffold-test-auth-10080` | 基于 RBAC 公共数据层与 Sa-Token WebFlux 的认证服务 | `10080` |
| `scaffold-test-provider-10081` | 注册到 Nacos 的 HTTP 服务提供者 | `10081` |
| `scaffold-test-consumer-10082` | 通过 Spring Cloud LoadBalancer 调用 HTTP 服务的消费者 | `10082` |
| `scaffold-test-order-10083` | 参与 Seata 全局事务的订单服务 | `10083` |
| `scaffold-test-gateway-10000` | 为 Provider 和 Consumer 提供路由转发的 WebFlux 网关 | `10000` |
| `scaffold-test-dubbo-api` | 共享的 `GreetingService` RPC 接口 | - |
| `scaffold-test-dubbo-provider-10881` | 注册到 Nacos 的 Dubbo 服务提供者 | `10881` |
| `scaffold-test-dubbo-consumer-10092` | 提供 HTTP 验证接口的 Dubbo 服务消费者 | `10092` |

`scaffold-dependencies-cloud` 是云服务示例的通用依赖模块，统一提供 Actuator
与 OpenTelemetry tracing。各服务仍按需声明 Web、Nacos、Gateway、Dubbo、Seata
和 Sentinel 等特定能力，避免引入不需要的运行时依赖。

## Nacos

使用服务发现的示例应先启动 Nacos。统一容器资源位于仓库根目录 `docker`：

```bash
./docker/cloud-compose.sh infra-up
```

该目录会同时启动 Nacos、Seata、Sentinel、Prometheus 和 Grafana。若使用外部
Nacos，或账号密码不同，再设置以下环境变量：

默认 Nacos 使用内置 Derby。若要使用 MySQL 持久化：

```bash
./docker/cloud-compose.sh infra-up-mysql
```

```bash
export NACOS_SERVER_ADDR=localhost:8848
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos
```

## 统一链路追踪

所有可执行服务均接入 Micrometer Tracing 的 OpenTelemetry bridge，并使用 W3C
Trace Context 的 `traceparent` 传递上下文。HTTP 服务、Gateway 和 `RestClient`
调用由 Spring Boot 自动创建与透传；Dubbo 3 使用内置的 Micrometer Observation
Filter 在 RPC 调用中继续同一条链路。

日志会输出当前请求的 `traceId` 和 `spanId`。开发环境采样率设为 `1.0`，生产环境
应按流量和观测成本调整。

可使用固定的 `traceparent` 验证入口服务会接收 W3C 上下文：

```bash
curl -H 'traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01' \
  'http://localhost:10081/api/echo?message=trace-test'
```

## Spring Cloud HTTP 示例

先启动 `scaffold-test-provider-10081`，再启动
`scaffold-test-consumer-10082`。

```bash
curl 'http://localhost:10082/api/provider-echo?message=hello'
```

消费者通过 Nacos 发现 `cloud-provider-10081`，再经 Spring Cloud LoadBalancer
调用其 `/api/echo` 接口。

## Gateway 示例

启动 HTTP Provider 和 Consumer 后，再启动
`scaffold-test-gateway-10000`。

```bash
curl 'http://localhost:10000/provider/api/echo?message=hello'
curl 'http://localhost:10000/consumer/api/provider-echo?message=hello'
```

网关使用 `lb://` 路由，并在转发前移除 `/provider` 或 `/consumer` 的首段路径。

## Auth 示例

`scaffold-test-auth-10080` 是薄启动模块，依赖 `scaffold-module-rbac-auth-sa-webflux`。
该模块复用 `scaffold-module-rbac-data` 中的用户、角色、关联表和 MyBatis Mapper，
并通过 `scaffold-starter-sa-token-webflux` 提供 WebFlux 登录能力。Gateway 已将
`/auth/**` 路由到 `cloud-auth`。

默认读取 `training` 库，可通过环境变量覆盖：

```bash
export RBAC_DATASOURCE_URL='jdbc:mysql://localhost:3306/training?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai'
export RBAC_DATASOURCE_USERNAME=root
export RBAC_DATASOURCE_PASSWORD=123456
```

启动 Auth 和 Gateway 后登录：

```bash
curl -X POST 'http://localhost:10000/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

响应中的 `tokenValue` 可作为后续请求的 Bearer token：

```bash
curl 'http://localhost:10000/auth/token-info' \
  -H 'Authorization: Bearer <tokenValue>'
```

## Sentinel 示例

Gateway 和 Consumer 都已接入 Sentinel。默认 Sentinel Dashboard 地址为
`localhost:8858`，可通过 `SENTINEL_DASHBOARD` 覆盖。

Gateway 对 `cloud-provider` 和 `cloud-consumer` 两个路由设置本地限流规则：
每个路由每秒最多 5 次请求。超过阈值时会直接返回 `429` JSON 响应：

```bash
for i in {1..10}; do
  curl -i 'http://localhost:10000/provider/api/echo?message=gateway-sentinel'
done
```

Consumer 在 `/api/provider-echo` 入口使用 `@SentinelResource` 声明业务资源
`consumer-provider-echo`，并在启动时加载两条规则：

- 流控规则：每秒最多 2 次请求，触发后进入 `blockHandler`
- 熔断规则：10 秒统计窗口内最少 5 次请求，异常比例超过 50% 后熔断 10 秒

直接访问 Consumer 可以验证方法级限流：

```bash
for i in {1..6}; do
  curl 'http://localhost:10082/api/provider-echo?message=consumer-sentinel'
done
```

被 Sentinel 限流时，响应数据中会出现 `sentinel=blocked`；下游调用异常并触发降级时，
响应数据中会出现 `sentinel=fallback`。

## Seata 示例

该示例以 `scaffold-test-consumer-10082` 为全局事务发起方，并让 Provider、Consumer、
Order 分别写入 `scaffold_provider`、`scaffold_consumer`、`scaffold_order` 数据库。
三个模块都引入 `scaffold-starter-orm`，使用 JPA Entity 和 `JpaRepository` 完成
业务表建表及 CRUD；三个库还各有一张 AT 模式所需的 `undo_log` 表。

先创建数据库和 `undo_log`：

```bash
mysql -uroot -p < docker/mysql/seata-demo.sql
```

应用启动时，Hibernate 根据三个 `@Entity` 自动创建或更新 `provider_tx_record`、
`consumer_tx_record`、`order_tx_record`，该行为由各模块的
`spring.jpa.hibernate.ddl-auto` 控制，默认值为 `update`。

再启动 Nacos、Seata Server 以及三个业务服务。Seata 使用以下配置：

- 注册信息：`application=seata-server`、`group=SEATA_GROUP`
- 配置：`dataId=seataServer.properties`、`group=SEATA_GROUP`
- 事务组：`scaffold-seata-tx-group` 映射到 Seata 集群 `default`

使用 [`docker`](../docker) 启动基础设施时，会自动将
[`seataServer.properties`](../docker/seata/seataServer.properties)
发布到 Nacos，Data ID 为 `seataServer.properties`，Group 为 `SEATA_GROUP`。

分别启动 `CloudProviderApplication`、`CloudOrderApplication`、
`CloudConsumerApplication` 后，使用不同的业务键验证提交和回滚。

成功提交后三个计数均为 `1`：

```bash
curl -X POST 'http://localhost:10082/api/seata/transactions/tx-success-001'
curl 'http://localhost:10082/api/seata/transactions/tx-success-001/counts'
```

失败请求会在三个分支全部写入后抛出异常；随后查询时三个计数都应为 `0`：

```bash
curl -X POST 'http://localhost:10082/api/seata/transactions/tx-rollback-001?fail=true'
curl 'http://localhost:10082/api/seata/transactions/tx-rollback-001/counts'
```

Consumer 在编排方法上同时使用 `@GlobalTransactional` 和 `@Transactional`。
当前 HTTP Interface 显式传递 `TX_XID` 请求头，Provider 和 Order 的 Seata MVC
拦截器负责绑定及清理 XID，各分支在本地事务中通过 JPA `saveAndFlush` 写入业务表。

先启动统一基础设施：

```bash
./docker/cloud-compose.sh infra-up
./docker/cloud-compose.sh logs infra seata-server
```

该 Server 会通过容器网络访问 `nacos:8848`，并以 `SEATA_GROUP` 下的
`seata-server` 注册，同时从该组的 `seataServer.properties` 读取配置。
客户端不再直连 `127.0.0.1:8091`，而是从 Nacos 发现可用的 Seata Server。

默认 Nacos 账号配置见 `docker/cloud.env`。服务地址、账号、密码、命名空间、组和 Data ID 可分别
通过 `NACOS_SERVER_ADDR`、`NACOS_USERNAME`、`NACOS_PASSWORD`、`NACOS_NAMESPACE`、
`SEATA_NACOS_GROUP`、`SEATA_NACOS_DATA_ID` 覆盖；修改 Server 侧认证或命名空间时，也应
同步更新 [`docker/seata/application.yml`](../docker/seata/application.yml)。

## Dubbo 示例

先启动 `scaffold-test-dubbo-provider-10881`，再启动
`scaffold-test-dubbo-consumer-10092`。

```bash
curl 'http://localhost:10092/api/dubbo/greet?name=Codex'
```

消费者通过 Nacos 查找 `GreetingService`，再使用 Dubbo 调用 Provider。

## 构建

在仓库根目录构建全部云服务示例：

```bash
./mvnw -Pexamples-cloud \
  -pl :scaffold-test-auth-10080,:scaffold-test-provider-10081,:scaffold-test-consumer-10082,:scaffold-test-order-10083,:scaffold-test-gateway-10000,:scaffold-test-dubbo-provider-10881,:scaffold-test-dubbo-consumer-10092 \
  -am compile
```

## Docker Compose 多实例运行

先在宿主机启动 Nacos。Compose 中的容器默认通过
`host.docker.internal:8848` 连接它；Linux Docker Engine 也由 Compose 的
`host-gateway` 映射支持该地址。若 Nacos 地址或认证信息不同，在运行前覆盖环境变量：

```bash
export NACOS_SERVER_ADDR=nacos.example.internal:8848
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos
```

从仓库根目录启动全部服务，每个服务启动两个实例：

```bash
./docker/cloud-compose.sh services-up
```

指定三副本：

```bash
./docker/cloud-compose.sh services-up 3
```

脚本会横向扩容 Provider、Consumer、Order、Gateway、Dubbo Provider 和 Dubbo Consumer。
容器之间使用 Nacos 发现彼此，因此应用端口可以相同；每个容器都拥有
独立 IP。Gateway 的 `10000` 会被 Docker 为每个副本随机映射到一个宿主机端口，可查看入口：

```bash
./docker/cloud-compose.sh ps
# 或 docker compose -f docker/compose.services.yml port cloud-gateway 10000
```

然后使用上一步输出的任一端口访问，例如：

```bash
curl 'http://localhost:随机端口/provider/api/echo?message=hello'
```

查看日志或停止环境：

```bash
./docker/cloud-compose.sh logs services cloud-provider
./docker/cloud-compose.sh down
```

## Prometheus + Grafana 监控

云服务示例通过 `scaffold-dependencies-cloud` 统一引入 Actuator 和 Prometheus
registry。各 HTTP 服务会暴露：

```text
/actuator/prometheus
```

推荐先启动统一基础设施：

```bash
./docker/cloud-compose.sh infra-up
```

访问地址：

```text
Prometheus: http://localhost:9090
Grafana:    http://localhost:3000
```

Grafana 默认账号密码为 `admin/admin`，Prometheus 数据源会自动配置为
`http://prometheus:9090`。可在
[`docker/cloud.env`](../docker/cloud.env) 中覆盖端口或账号：

```bash
export PROMETHEUS_PORT=19090
export GRAFANA_PORT=13000
export GRAFANA_ADMIN_USER=admin
export GRAFANA_ADMIN_PASSWORD=scaffold
```

在 Prometheus 的 `Status -> Target health` 页面应能看到这些 job：

- `cloud-provider`
- `cloud-consumer`
- `cloud-order`
- `cloud-gateway`
- `dubbo-consumer`
- `prometheus`
- `nacos`
- `seata-server`

也可以直接查询指标：

```promql
up
jvm_memory_used_bytes
http_server_requests_seconds_count
```

Grafana 可以直接导入社区 Spring Boot / Micrometer dashboard，例如 JVM
Micrometer 或 Spring Boot 3 Observability 类 dashboard，然后选择默认的
`Prometheus` 数据源。

当前 `dubbo-provider` 只开放 Dubbo 协议端口 `10881`，没有 HTTP Actuator
端口，所以 Prometheus 默认不抓取它。若需要监控 Dubbo Provider 的 JVM 指标，
需要为该模块增加 Web/management HTTP 端口，再把目标加入
[`docker/prometheus/prometheus.yml`](../docker/prometheus/prometheus.yml)。
