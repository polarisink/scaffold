FROM adoptopenjdk/openjdk11:ubi
MAINTAINER aries 1952482944@qq.com
VOLUME /tmp
ADD target/*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar -Duser.timezone=GMT+08 /app.jar","-Dspring.profiles.active=prod" ]