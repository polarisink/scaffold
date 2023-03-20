package github.polarisink.api.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

/**
 * 使用nio和bio下载文件,经过测试nio更快
 *
 * @author aries
 * @date 2022/9/26
 */
@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadHandler {

    String filePath = "C:\\Users\\hzsk\\Desktop\\apache-maven-3.8.6-bin.zip";

    private static String buildName(String name) {
        return "attachment;filename*=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
    }

    @GetMapping("/nio")
    public long downloadExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //下面是源文件的路径，也可以自己定义，例如："E:\BaiduNetdiskDownload\1-5 网站前台 活动与招聘.zip
        File file = new File(filePath);
        LOG.info("导入模板路径:{}", filePath);
        try (FileInputStream fin = new FileInputStream(file); FileChannel channel = fin.getChannel()) {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            long fileLength = file.length();
            LOG.warn("文件大小: {}MB", fileLength >> 20);
            //对中文名进行编码，解决中文乱码
            response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
            response.setHeader(CONTENT_DISPOSITION, buildName(file.getName()));
            response.setHeader(CONTENT_LENGTH, String.valueOf(fileLength));
            response.setContentType(APPLICATION_OCTET_STREAM_VALUE);
            int buffSize = 1 << 10;
            ByteBuffer buffer = ByteBuffer.allocate(1 << 12);
            byte[] byteArr = new byte[buffSize];
            int nGet;
            while (channel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    nGet = Math.min(buffer.remaining(), buffSize);
                    // read bytes from disk
                    buffer.get(byteArr, 0, nGet);
                    // write bytes to output
                    response.getOutputStream().write(byteArr);
                }
                buffer.clear();
            }
        } catch (Exception e) {
            LOG.error("下载Excel模板失败");
            e.printStackTrace();
        }
        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        return lastTaskTimeMillis;
    }

    @RequestMapping("/bio")
    public long fileDownLoad(HttpServletResponse response) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        File file = new File(filePath);
        if (!file.exists()) {
            return -1;
        }
        response.reset();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentLength((int) file.length());
        response.setHeader(CONTENT_DISPOSITION, buildName(file.getName()));
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return -1;
        }
        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        return lastTaskTimeMillis;
    }
}
