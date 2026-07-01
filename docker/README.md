# 本地容器环境

仓库的 Docker Compose 与容器辅助脚本统一放在本目录。Cloud 基础设施和业务
Compose 平铺在 `docker/` 下，`flink/` 保留独立目录存放 Flink Session 集群配置。

## Cloud 基础设施

在仓库根目录执行：

```bash
# Nacos（Derby）、Seata、Sentinel、Prometheus、Grafana
./docker/cloud-compose.sh infra-up

# 使用 MySQL 持久化 Nacos
./docker/cloud-compose.sh infra-up-mysql
```

访问地址：

| 服务 | 地址 | 默认账号 |
| --- | --- | --- |
| Nacos Console | <http://localhost:8080> | `nacos/nacos`（开启认证时） |
| Sentinel | <http://localhost:8858> | `sentinel/sentinel` |
| Seata | `localhost:8091` | - |
| Prometheus | <http://localhost:9090> | - |
| Grafana | <http://localhost:3000> | `admin/admin` |

版本、端口和账号可在 `docker/cloud.env` 中修改，也可以用同名环境变量覆盖。
三库 Seata AT 回滚示例的数据库与 `undo_log` 初始化 SQL 位于
`docker/mysql/seata-demo.sql`；业务表由各服务的 JPA Entity 自动创建。

## Cloud 业务服务

基础设施就绪后执行：

```bash
./docker/cloud-compose.sh services-up       # 默认每个服务 2 副本
./docker/cloud-compose.sh services-up 3     # 每个服务 3 副本
./docker/cloud-compose.sh ps
```

脚本会创建共享的 `scaffold-cloud` 网络，使 Prometheus 能通过服务名抓取业务指标。
业务容器通过 `host.docker.internal` 访问宿主机暴露的 Nacos 和 Sentinel；Linux
Docker Engine 由 Compose 的 `host-gateway` 映射支持。Gateway 使用随机宿主机端口，
`ps` 命令会列出实际入口。

日志与清理：

```bash
./docker/cloud-compose.sh logs infra seata-server
./docker/cloud-compose.sh logs services cloud-provider
./docker/cloud-compose.sh down
```

如需连同持久化数据卷一起清理，可直接执行：

```bash
docker compose -p scaffold-cloud-infra \
  --env-file docker/cloud.env \
  -f docker/compose.infrastructure.yml \
  -f docker/compose.mysql.yml down -v
```

## Flink

```bash
./docker/flink/flink-compose.sh up
./docker/flink/flink-compose.sh submit
./docker/flink/flink-compose.sh down
```
