FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace
COPY . .

ARG MODULE
RUN test -n "$MODULE" \
    && mvn -Pexamples-cloud -pl ":${MODULE}" -am -DskipTests install \
    && mvn -Pexamples-cloud -pl ":${MODULE}" -DskipTests package spring-boot:repackage \
    && cp "$(find "${MODULE}/target" -maxdepth 1 -type f -name "${MODULE}-*.jar" ! -name 'original-*.jar' | head -n 1)" /tmp/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /tmp/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
