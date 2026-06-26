#!/bin/sh
set -eu

NACOS_ADDR="${NACOS_ADDR:-http://nacos:8848}"
NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-nacos}"
NACOS_AUTH_ENABLE="${NACOS_AUTH_ENABLE:-false}"

token=""
echo "Waiting for Nacos at ${NACOS_ADDR}"
for i in $(seq 1 60); do
  if curl -fsS "${NACOS_ADDR}/nacos/actuator/health" >/dev/null 2>&1; then
    break
  fi
  if [ "$i" = "60" ]; then
    echo "Nacos did not become ready in time" >&2
    exit 1
  fi
  sleep 2
done

if [ "$NACOS_AUTH_ENABLE" = "true" ]; then
  echo "Logging in to Nacos at ${NACOS_ADDR}"
  login_response="$(curl -fsS -X POST "${NACOS_ADDR}/nacos/v1/auth/users/login" \
    -d "username=${NACOS_USERNAME}" \
    -d "password=${NACOS_PASSWORD}")"
  token="$(printf '%s' "$login_response" | sed -n 's/.*"accessToken"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')"
  test -n "$token"
fi

publish_config() {
  data_id="$1"
  group="$2"
  file="$3"
  url="${NACOS_ADDR}/nacos/v1/cs/configs"
  if [ -n "$token" ]; then
    url="${url}?accessToken=${token}"
  fi

  echo "Publishing ${data_id} to Nacos group ${group}"
  curl -fsS -X POST "$url" \
    -F "dataId=${data_id}" \
    -F "group=${group}" \
    -F "type=properties" \
    -F "content=<${file}"
  echo
}

publish_config "seataServer.properties" "SEATA_GROUP" "/configs/seataServer.properties"
echo "Nacos config initialization complete"
