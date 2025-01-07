FROM openjdk:21
# 指定端口
EXPOSE 8082
# 工作目录
VOLUME /tmp
# 将jar和配置文件拷贝进容器
COPY ./target/*.jar app.jar
COPY ./src/resources/application.yml /conf/application.yml
COPY ./src/resources/application-dev.yml /conf/application-dev.yml
# 指定环境运行
# -XX:+UseZGC -XX:+ZGenerational
ENTRYPOINT ["java","-Dspring.config.location=/conf/application.yml","-jar","./app.jar"]