#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
APP_NAME="ScaffoldBiz"
APP_VERSION="1.0.0"
MODULE_NAME="scaffold-biz"
JAR_NAME="${MODULE_NAME}-1.0-SNAPSHOT.jar"
BUILD_DIR="${SCRIPT_DIR}/target"
JAR_SOURCE="${BUILD_DIR}/${JAR_NAME}"
INPUT_DIR="${BUILD_DIR}/jpackage-input"
APP_IMAGE_DIR="${BUILD_DIR}/jpackage-app-image"
JPACKAGE_DIR="${SCRIPT_DIR}/target/jpackage"
APP_IMAGE="${APP_IMAGE_DIR}/${APP_NAME}.app"
DMG_FILE="${JPACKAGE_DIR}/${APP_NAME}-${APP_VERSION}.dmg"

if [[ "$(uname -s)" != "Darwin" ]]; then
  echo "jpackage --type dmg only supports macOS." >&2
  exit 1
fi

if ! command -v jpackage >/dev/null 2>&1; then
  echo "jpackage was not found. Please run this script with a JDK that includes jpackage." >&2
  exit 1
fi

if [[ -z "${JAVA_HOME:-}" || ! -x "${JAVA_HOME}/bin/java" ]]; then
  if /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
    export JAVA_HOME
    JAVA_HOME="$(/usr/libexec/java_home -v 21)"
  fi
fi

cd "${PROJECT_DIR}"
MAVEN_CMD=""
for candidate in "${HOME}"/.m2/wrapper/dists/apache-maven-*-bin/*/apache-maven-*/bin/mvn; do
  if [[ -x "${candidate}" ]]; then
    MAVEN_CMD="${candidate}"
  fi
done

if [[ -n "${MAVEN_CMD}" ]]; then
  "${MAVEN_CMD}" -pl "${MODULE_NAME}" -am -DskipTests package
elif [[ -x ./mvnw ]]; then
  ./mvnw -pl "${MODULE_NAME}" -am -DskipTests package
elif command -v mvnd >/dev/null 2>&1; then
  mvnd -pl "${MODULE_NAME}" -am -DskipTests package
elif command -v mvn >/dev/null 2>&1; then
  mvn -pl "${MODULE_NAME}" -am -DskipTests package
else
  echo "Maven was not found. Please install Maven or keep the Maven wrapper in the project root." >&2
  exit 1
fi

rm -rf "${INPUT_DIR}" "${APP_IMAGE_DIR}" "${JPACKAGE_DIR}"
mkdir -p "${INPUT_DIR}" "${APP_IMAGE_DIR}" "${JPACKAGE_DIR}"
cp "${JAR_SOURCE}" "${INPUT_DIR}/"

jpackage \
  --type app-image \
  --name "${APP_NAME}" \
  --app-version "${APP_VERSION}" \
  --vendor "scaffold" \
  --dest "${APP_IMAGE_DIR}" \
  --input "${INPUT_DIR}" \
  --main-jar "${JAR_NAME}" \
  --main-class org.springframework.boot.loader.launch.JarLauncher \
  --mac-package-name "${APP_NAME}" \
  --mac-package-identifier "com.scaffold.biz" \
  --java-options "-Dfile.encoding=UTF-8" \
  --java-options "-Djava.awt.headless=true"

/usr/libexec/PlistBuddy -c "Add :LSUIElement bool true" "${APP_IMAGE}/Contents/Info.plist" >/dev/null 2>&1 \
  || /usr/libexec/PlistBuddy -c "Set :LSUIElement true" "${APP_IMAGE}/Contents/Info.plist"

jpackage \
  --type dmg \
  --name "${APP_NAME}" \
  --app-version "${APP_VERSION}" \
  --vendor "scaffold" \
  --dest "${JPACKAGE_DIR}" \
  --app-image "${APP_IMAGE}" \
  --mac-package-name "${APP_NAME}" \
  --mac-package-identifier "com.scaffold.biz"

cat <<EOF

Created:
  ${DMG_FILE}

Install:
  Open the DMG and drag ${APP_NAME}.app to Applications.

Uninstall:
  Stop the running app, then delete /Applications/${APP_NAME}.app.
EOF
