package com.polarisink.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Margin;
import com.microsoft.playwright.options.Media;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * pdf服务
 * <p>
 * # 指定下载路径
 * export PLAYWRIGHT_BROWSERS_PATH=/opt/playwright-browsers
 * mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
 */
@Component
public class PdfService implements ApplicationRunner, DisposableBean {
    private Playwright playwright;
    private Browser browser;

    /**
     * 生成pdf byte数组
     *
     * @param htmlContent html文本
     * @return pdf byte数组
     */
    public byte[] generatePdfFromHtml(String htmlContent) {
        // 每个请求开启独立的上下文和页面，保证线程安全
        try (BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            // 1. 加载 HTML 内容
            page.setContent(htmlContent);

            // 2. 等待网络空闲（如果 HTML 中包含远程图片或 JS 加载的图表）
            page.waitForLoadState(LoadState.NETWORKIDLE);
            //有些网页 CSS 是分 screen 和 print 的，PDF 默认使用 print。如果要 PDF 看起来和网页一模一样，可以设置
            page.emulateMedia(new Page.EmulateMediaOptions().setMedia(Media.SCREEN));
            // 3. 配置打印参数
            Page.PdfOptions options = new Page.PdfOptions()
                    .setFormat("A4")
                    .setPrintBackground(true) // 打印背景色和图片

                    .setMargin(new Margin().setTop("20mm").setBottom("20mm"));

            return page.pdf(options);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        playwright = Playwright.create();
        // 建议使用无头模式
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }
}