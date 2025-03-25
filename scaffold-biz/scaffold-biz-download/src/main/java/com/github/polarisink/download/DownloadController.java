package com.github.polarisink.download;

import com.github.linyuzai.download.core.annotation.Download;
import com.github.linyuzai.download.core.options.DownloadOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class DownloadController {

    private static final String classPathFile = "application.yml";
    private static final String filePath = "C:\\Users\\lqsgo\\Desktop\\dify.md";
    private static final String remoteUrl = "https://img2.baidu.com/it/u=3305049985,1754771021&fm=253&fmt=auto&app=138&f=JPEG?w=701&h=500";

    /**
     * classpath下的文件。
     */
    @Download(source = "classpath:" + classPathFile, filename = classPathFile)
    @GetMapping("/classpath")
    public void classpath() {
    }

    /**
     * 指定路径的文件。可以指定文件夹，会自动压缩整个文件夹。
     */
    @Download(source = "file:" + filePath, filename = "dify.md")
    @GetMapping("/file")
    public void file() {
    }

    /**
     * 指定下载地址。可以通过配置缓存下载文件，后面不需要重复下载。
     */
    @Download(filename = "remote.jpg", source = remoteUrl)
    @GetMapping("/http")
    public void http() {
    }

    /**
     * 返回指定路径的文件。可以指定文件夹，会自动压缩整个文件夹。
     */
    @Download
    @GetMapping("/file2")
    public File file2() {
        return new File(filePath);
    }

    /**
     * 支持组合不同类型的文件，会自动压缩所有文件。
     */
    @Download(filename = "List.zip")
    @GetMapping("/list")
    public List<Object> list() {
        List<Object> list = new ArrayList<>();
        list.add(new File(filePath));
        list.add(new ClassPathResource(classPathFile));
        list.add(remoteUrl);
        return list;
    }

    @Download
    @GetMapping("/rewrite")
    public DownloadOptions.Configurer rewrite() {
        return options -> {
            options.setFilename("remote.jpg");
            options.setSource(remoteUrl);
            options.setSourceCacheEnabled(false);
        };
    }
}
