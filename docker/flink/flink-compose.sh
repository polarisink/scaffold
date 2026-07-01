#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_DIR="$(cd -- "$SCRIPT_DIR/../.." && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
ENV_FILE="$SCRIPT_DIR/flink.env"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-scaffold-flink}"
JOB_CLASS="com.scaffold.flink.WordCountJob"

usage() {
  cat <<'EOF'
用法：
  ./docker/flink/flink-compose.sh up                 启动 Flink Session 集群
  ./docker/flink/flink-compose.sh submit [文本]      构建并提交 WordCount 作业
  ./docker/flink/flink-compose.sh scale <副本数>     调整 TaskManager 数量
  ./docker/flink/flink-compose.sh logs               查看 TaskManager 作业输出
  ./docker/flink/flink-compose.sh ps                 查看集群容器
  ./docker/flink/flink-compose.sh down               停止并删除集群容器
EOF
}

compose() {
  docker compose --env-file "$ENV_FILE" --project-name "$PROJECT_NAME" --file "$COMPOSE_FILE" "$@"
}

case "${1:-}" in
  up)
    compose up --detach
    echo "Flink Web UI: http://localhost:$(compose port jobmanager 8081 | sed 's/.*://')"
    ;;
  submit)
    text="${2:-hello flink hello scaffold}"
    "$REPOSITORY_DIR/mvnw" -f "$REPOSITORY_DIR/pom.xml" \
      -pl :scaffold-test-flink -am -DskipTests package
    jar_file="$(find "$REPOSITORY_DIR/scaffold-test/scaffold-test-flink/target" \
      -maxdepth 1 -type f -name 'scaffold-test-flink-*.jar' ! -name 'original-*.jar' | head -n 1)"
    test -n "$jar_file" || { echo "未找到 Flink 作业 JAR" >&2; exit 1; }
    compose exec -T jobmanager flink run \
      --class "$JOB_CLASS" "/workspace/target/$(basename "$jar_file")" \
      --text "$text"
    ;;
  scale)
    replicas="${2:-}"
    [[ "$replicas" =~ ^[1-9][0-9]*$ ]] || { echo "副本数必须是正整数" >&2; exit 2; }
    compose up --detach --scale taskmanager="$replicas"
    ;;
  logs)
    compose logs --follow taskmanager
    ;;
  ps)
    compose ps
    ;;
  down)
    compose down
    ;;
  -h|--help|help|'')
    usage
    ;;
  *)
    usage >&2
    exit 2
    ;;
esac
