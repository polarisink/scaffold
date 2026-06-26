# Scaffold Test Cloud Containers

本目录用于启动 `scaffold-test-cloud` 的本地基础设施：

- Nacos，默认使用内置 Derby，也可切换到 MySQL
- Seata Server，并自动把 `seataServer.properties` 发布到 Nacos
- Sentinel Dashboard
- Prometheus
- Grafana

Nacos 默认模式参考 `nacos-docker/example/standalone-derby.yaml`，直接使用内置
Derby，适合本地快速验证。MySQL 模式参考
`nacos-docker/example/standalone-mysql.yaml` 和 `mysql-init.sh`：先根据
`NACOS_VERSION` 下载对应版本的 `mysql-schema.sql`，再由 MySQL 首次启动时执行。

## 启动基础设施（Derby）

```bash
cd scaffold-test-cloud/container
docker compose up -d
```

## 启动基础设施（MySQL）

```bash
cd scaffold-test-cloud/container
docker compose -f docker-compose.yml -f docker-compose.mysql.yml up -d
```

MySQL 端口、库名、账号和密码可在 `.env` 中调整。首次从 Derby 切换到 MySQL
或反向切换时，建议先清理旧 volume，避免 Nacos 历史数据造成误判：

```bash
docker compose down -v
docker compose -f docker-compose.yml -f docker-compose.mysql.yml down -v
```

访问地址：

```text
Nacos Console:      http://localhost:8080
Nacos Server:       localhost:8848
Sentinel Dashboard: http://localhost:8858
Seata Server:       localhost:8091
Prometheus:         http://localhost:9090
Grafana:            http://localhost:3000
```

默认账号密码：

```text
Nacos:   nacos/nacos
Sentinel: sentinel/sentinel
Grafana: admin/admin
MySQL:   root/root, nacos/nacos（仅 MySQL 模式）
```

端口、账号、版本和 MySQL 连接信息可在 `.env` 中调整。

## 启动云服务示例

基础设施启动后，回到 `scaffold-test-cloud` 目录启动业务服务：

```bash
cd ..
export NACOS_SERVER_ADDR=host.docker.internal:8848
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=nacos
export SENTINEL_DASHBOARD=host.docker.internal:8858

./scripts/cloud-compose.sh up
```

若在 Linux Docker Engine 下运行，现有业务 compose 已配置
`host.docker.internal:host-gateway`，因此同样可以使用上面的环境变量。

## 监控

Prometheus 会抓取：

- Nacos: `nacos:8848/nacos/actuator/prometheus`
- Seata Server: `seata-server:9898`
- 业务服务: `/actuator/prometheus`

Grafana 会自动配置 Prometheus 数据源：`http://prometheus:9090`。

## 清理

停止容器但保留数据：

```bash
docker compose down
```

彻底清理 MySQL、Nacos 日志、Prometheus、Grafana 数据：

```bash
docker compose down -v
```
