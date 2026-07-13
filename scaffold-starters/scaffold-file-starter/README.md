# scaffold-file-starter

提供本地磁盘与 S3 兼容对象存储的文件上传能力。

## 接入

```xml
<dependency>
    <groupId>com.scaffold</groupId>
    <artifactId>scaffold-file-starter</artifactId>
</dependency>
```

本地存储示例：

```yaml
scaffold:
  file-storage:
    enabled: true
    type: local
    access-prefix: /files/
    local:
      base-path: ./www
      access-path: /files/**
```

S3 兼容存储示例：

```yaml
scaffold:
  file-storage:
    enabled: true
    type: s3
    access-prefix: https://cdn.example.com/
    s3:
      endpoint: https://s3.example.com
      access-key: ${S3_ACCESS_KEY}
      secret-key: ${S3_SECRET_KEY}
      bucket-name: scaffold
      region: us-east-1
```

启用后提供 `GET /file/access`、`POST /file/upload` 和 `POST /file/upload-folder`。本地公开路径应加入认证与请求日志白名单；生产环境不要把密钥写入仓库，并应在网关层限制文件大小与类型。
