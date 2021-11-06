# 个人SpringBoot项目脚手架

## 文档系统

> 使用`swagger3`官方提供的starter和`Knife4j`可以很方便的进行文档管理
> 集成maven依赖之后不需要其他配置即可访问
> swagger: `http://ip:port/swagger-ui/`, knife: `http://ip:port/doc.html`

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>${swagger.version}</version>
</dependency>
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>${knife4j.version}</version>
</dependency>
```

## 日志系统

> 基于[docker-elk](https://github.com/deviantony/docker-elk) 快速搭建elk系统进行日志管理，实现日志搜索功能

- docker-elk

```shell
git clone https://github.com/deviantony/docker-elk.git
cd docker-elk
docker-compose up
```

- logstash

> 引入logstash依赖,配置好logback-spring.xml,application.yml中指定好即可

- 打印http请求

## 统一结果返回,全局异常处理

```markdown

```

##  








