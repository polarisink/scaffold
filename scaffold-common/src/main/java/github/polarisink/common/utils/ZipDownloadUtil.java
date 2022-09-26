package github.polarisink.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * @author hzsk
 */
@Slf4j
public class ZipDownloadUtil {
  /**
   * 获取当前系统的临时目录
   */
  private static final String FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator;

  private static final int ZIP_BUFFER_SIZE = 8192;

  /**
   * zip打包下载
   *
   * @param response
   * @param zipFileName
   * @param fileList
   */
  public static void zipDownload(HttpServletResponse response, String zipFileName, List<File> fileList) {
    // zip文件路径
    String zipPath = FILE_PATH + zipFileName;
    try {
      //创建zip输出流
      try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath))) {
        //声明文件集合用于存放文件
        byte[] buffer = new byte[1024];
        //将文件放入zip压缩包
        for (File file : fileList) {
          try (FileInputStream fis = new FileInputStream(file)) {
            out.putNextEntry(new ZipEntry(file.getName()));
            int len;
            // 读入需要下载的文件的内容，打包到zip文件
            while ((len = fis.read(buffer)) > 0) {
              out.write(buffer, 0, len);
            }
            out.closeEntry();
          }
        }
      }
      //下载zip文件
      downFile(response, zipFileName);
    } catch (Exception e) {
      LOG.error("文件下载出错", e);
    } finally {
      // zip文件也删除
      fileList.add(new File(zipPath));
      deleteFile(fileList);
    }
  }


  /**
   * 文件下载
   *
   * @param response
   * @param zipFileName
   */
  private static void downFile(HttpServletResponse response, String zipFileName) {
    try {
      String path = FILE_PATH + zipFileName;
      File file = new File(path);
      if (file.exists()) {
        try (InputStream ins = new FileInputStream(path); BufferedInputStream bins = new BufferedInputStream(ins); OutputStream outs = response.getOutputStream(); BufferedOutputStream bouts = new BufferedOutputStream(outs)) {
          response.setContentType("application/x-download");
          response.setHeader(CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(zipFileName, StandardCharsets.UTF_8));
          int bytesRead;
          byte[] buffer = new byte[ZIP_BUFFER_SIZE];
          while ((bytesRead = bins.read(buffer, 0, ZIP_BUFFER_SIZE)) != -1) {
            bouts.write(buffer, 0, bytesRead);
          }
          bouts.flush();
        }
      }
    } catch (Exception e) {
      LOG.error("文件下载出错", e);
    }
  }

  /**
   * 删除文件
   *
   * @param fileList
   * @return
   */
  public static void deleteFile(List<File> fileList) {
    for (File file : fileList) {
      if (file.exists()) {
        file.delete();
      }
    }
  }

  /**
   * 获取路径下的所有文件/文件夹
   *
   * @param directoryPath  需要遍历的文件夹路径
   * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
   * @return
   */
  public static List<String> getAllFile(String directoryPath, boolean isAddDirectory) {
    List<String> list = new ArrayList<>();
    File baseFile = new File(directoryPath);
    if (baseFile.isFile() || !baseFile.exists()) {
      return list;
    }
    File[] files = baseFile.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        if (isAddDirectory) {
          list.add(file.getAbsolutePath());
        }
        list.addAll(getAllFile(file.getAbsolutePath(), isAddDirectory));
      } else {
        list.add(file.getAbsolutePath());
      }
    }
    return list;
  }
}