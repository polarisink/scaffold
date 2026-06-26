#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
CLOUD_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
COMPOSE_FILE="$CLOUD_DIR/docker-compose.cloud.yml"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-scaffold-test-cloud}"

usage() {
  cat <<'EOF'
用法：
  ./scripts/cloud-compose.sh up [副本数]    构建并启动所有可扩容微服务
  ./scripts/cloud-compose.sh down           停止并删除容器
  ./scripts/cloud-compose.sh logs [服务名]  查看日志
  ./scripts/cloud-compose.sh ps             查看容器及 Gateway 映射端口

默认副本数为 2。Nacos 默认使用 host.docker.internal:8848。
可通过 NACOS_SERVER_ADDR、NACOS_USERNAME、NACOS_PASSWORD 覆盖。
EOF
}

compose() {
  docker compose --project-name "$PROJECT_NAME" --file "$COMPOSE_FILE" "$@"
}

case "${1:-}" in
  up)
    replicas="${2:-2}"
    [[ "$replicas" =~ ^[1-9][0-9]*$ ]] || { echo "副本数必须是正整数" >&2; exit 2; }
    compose up --detach --build --remove-orphans \
      --scale cloud-provider="$replicas" \
      --scale cloud-consumer="$replicas" \
      --scale cloud-gateway="$replicas" \
      --scale dubbo-provider="$replicas" \
      --scale dubbo-consumer="$replicas" \
      --scale cloud-seata="$replicas"
    echo
    echo "Gateway 入口端口："
    compose port cloud-gateway 10000
    ;;
  down)
    compose down
    ;;
  logs)
    shift
    compose logs --follow "$@"
    ;;
  ps)
    compose ps
    echo
    echo "Gateway 入口端口："
    compose port cloud-gateway 10000 || true
    ;;
  -h|--help|help|'')
    usage
    ;;
  *)
    usage >&2
    exit 2
    ;;
esac
