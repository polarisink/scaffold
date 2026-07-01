#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
INFRA_FILE="$SCRIPT_DIR/compose.infrastructure.yml"
MYSQL_FILE="$SCRIPT_DIR/compose.mysql.yml"
SERVICES_FILE="$SCRIPT_DIR/compose.services.yml"
ENV_FILE="$SCRIPT_DIR/cloud.env"
INFRA_PROJECT="${CLOUD_INFRA_PROJECT_NAME:-scaffold-cloud-infra}"
SERVICES_PROJECT="${CLOUD_SERVICES_PROJECT_NAME:-scaffold-test-cloud}"
NETWORK_NAME="${CLOUD_NETWORK:-$(sed -n 's/^CLOUD_NETWORK=//p' "$ENV_FILE" | tail -n 1)}"
NETWORK_NAME="${NETWORK_NAME:-scaffold-cloud}"

usage() {
  cat <<'EOF'
用法：
  ./docker/cloud-compose.sh infra-up                  启动 Derby 模式基础设施
  ./docker/cloud-compose.sh infra-up-mysql            启动 MySQL 模式基础设施
  ./docker/cloud-compose.sh services-up [副本数]      构建并启动业务服务，默认 2 副本
  ./docker/cloud-compose.sh ps                        查看全部容器及 Gateway 端口
  ./docker/cloud-compose.sh logs infra|services [服务] 查看日志
  ./docker/cloud-compose.sh down                      停止全部 Cloud 容器
EOF
}

infra_compose() {
  docker compose --env-file "$ENV_FILE" --project-name "$INFRA_PROJECT" --file "$INFRA_FILE" "$@"
}

mysql_compose() {
  docker compose --env-file "$ENV_FILE" --project-name "$INFRA_PROJECT" --file "$INFRA_FILE" --file "$MYSQL_FILE" "$@"
}

services_compose() {
  docker compose --env-file "$ENV_FILE" --project-name "$SERVICES_PROJECT" --file "$SERVICES_FILE" "$@"
}

ensure_network() {
  docker network inspect "$NETWORK_NAME" >/dev/null 2>&1 \
    || docker network create "$NETWORK_NAME" >/dev/null
}

case "${1:-}" in
  infra-up)
    ensure_network
    infra_compose up --detach
    ;;
  infra-up-mysql)
    ensure_network
    mysql_compose up --detach
    ;;
  services-up)
    replicas="${2:-2}"
    [[ "$replicas" =~ ^[1-9][0-9]*$ ]] || { echo "副本数必须是正整数" >&2; exit 2; }
    ensure_network
    services_compose up --detach --build --remove-orphans \
      --scale cloud-provider="$replicas" \
      --scale cloud-consumer="$replicas" \
      --scale cloud-order="$replicas" \
      --scale cloud-gateway="$replicas" \
      --scale dubbo-provider="$replicas" \
      --scale dubbo-consumer="$replicas"
    echo "Gateway 入口端口："
    services_compose port cloud-gateway 10000
    ;;
  ps)
    infra_compose ps
    services_compose ps
    echo "Gateway 入口端口："
    services_compose port cloud-gateway 10000 || true
    ;;
  logs)
    target="${2:-}"
    shift 2 || true
    case "$target" in
      infra) infra_compose logs --follow "$@" ;;
      services) services_compose logs --follow "$@" ;;
      *) echo "logs 需要指定 infra 或 services" >&2; exit 2 ;;
    esac
    ;;
  down)
    services_compose down
    mysql_compose down
    ;;
  -h|--help|help|'')
    usage
    ;;
  *)
    usage >&2
    exit 2
    ;;
esac
