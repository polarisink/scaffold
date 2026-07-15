# 注解式下载示例

演示 `concept-download` 从 classpath、本地文件、远程 URL 和混合资源生成下载响应，也包含直接使用 `StreamingResponseBody` 的对照实现。

```bash
./mvnw -pl scaffold-test/scaffold-test-download -am -Pexamples spring-boot:run
curl -OJ http://localhost:8083/classpath
curl -OJ http://localhost:8083/http
curl -OJ http://localhost:8083/list
```

| 接口 | 行为 |
| --- | --- |
| `/classpath` | 下载 `application.yml` |
| `/file`、`/file2` | 下载本地文件 |
| `/http`、`/rewrite` | 下载远程图片 |
| `/list` | 混合资源打包为 ZIP |
| `/download` | 流式读取本地文件 |

`/file`、`/file2` 和 `/download` 当前含 Windows 绝对路径，运行前必须在 `DownloadController` 中替换；`/http` 依赖外网。测试目录还包含 Word 表格转 Excel 的实验代码，与 Web 下载接口相互独立。
