# Playwright HTML 转 PDF

`PdfService` 在应用启动时创建无头 Chromium，将 HTML 渲染为带背景的 A4 PDF；每次调用使用独立 BrowserContext 和 Page，应用关闭时释放浏览器资源。

首次使用先安装与 Playwright Java 版本匹配的 Chromium：

```bash
./mvnw -pl scaffold-test/scaffold-test-playwright -am -Pexamples \
  exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args='install chromium'
./mvnw -pl scaffold-test/scaffold-test-playwright -am -Pexamples spring-boot:run
```

可用 `PLAYWRIGHT_BROWSERS_PATH` 指定共享浏览器目录。当前模块只提供 `PdfService.generatePdfFromHtml(String)`，没有 HTTP Controller；要对外下载 PDF，需要增加接口并设置 `Content-Type: application/pdf` 与附件文件名。远程图片或脚本会等到 `NETWORKIDLE`，不可访问的资源可能拖慢或阻塞渲染。
