# Flink 基础示例

本模块提供一个 DataStream WordCount 作业，展示 Flink 最基础的数据处理链路：

```text
内存数据源 -> flatMap 拆词 -> keyBy 分组 -> sum 聚合 -> print 输出
```

## 本地运行

运行单元测试：

```bash
./mvnw -pl scaffold-test/scaffold-test-flink -am -Pexamples test
```

在 IDE 中直接运行 `WordCountJob.main()` 时，Flink 依赖会加入本地运行时类路径。
构建产物是提交给 Flink 集群的普通作业 JAR，不是包含 Flink 运行时的 fat JAR，
因此不要使用 `java -jar` 直接启动该 JAR。

使用 Docker Session 集群时，在仓库根目录执行：

```bash
./docker/flink/flink-compose.sh up
./docker/flink/flink-compose.sh submit "hello flink hello scaffold"
./docker/flink/flink-compose.sh logs
```

Flink Web UI: <http://localhost:8081>

`submit` 会先使用 Maven 构建本模块，再将作业 JAR 提交到 Docker Compose
启动的 Session 集群。WordCount 是有界作业，完成后可在 TaskManager 日志中看到
`(word,count)` 输出。

常用命令：

```bash
./docker/flink/flink-compose.sh ps
./docker/flink/flink-compose.sh scale 2
./docker/flink/flink-compose.sh down
```

也可不使用脚本，直接操作 Compose：

```bash
docker compose -f docker/flink/docker-compose.yml up -d
docker compose -f docker/flink/docker-compose.yml down
```
