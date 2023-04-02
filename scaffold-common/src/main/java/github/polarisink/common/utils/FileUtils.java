package github.polarisink.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件工具类
 *
 * @author lqs
 * @date 2022/4/12
 */
@Slf4j
public class FileUtils {

  public static final String FILE_PATH = System.getProperty("user.home") + "/files";


  private static Charset charset = StandardCharsets.UTF_8;

  /**
   * 从简单csv文件读取list实体类
   *
   * @param path
   * @param tClass
   * @param <T>
   * @return /*
   */
  public static <T> List<T> getListFromLocalCsv(String path, Class<T> tClass) {
    LOG.info("home path, {}", FILE_PATH);
    File file = null;
    try {
      file = ResourceUtils.getFile(FILE_PATH + path);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    BufferedReader bufferedReader = ResourceUtil.getReader(Objects.requireNonNull(file).getAbsolutePath(),
        CharsetUtil.CHARSET_GBK);
    return CsvUtil.getReader().read(bufferedReader, tClass);
  }

  /**
   * 从文件按行读返回list字符串
   *
   * @param path 文件路径
   * @return
   */
  public static List<String> getFileContent(String path) {
    return getFileContent(new File(path));
  }

  /**
   * 从文件按行读返回list字符串
   *
   * @param file 文件
   * @return
   */
  public static List<String> getFileContent(File file) {
    List<String> strList = new ArrayList<>();
    /*@formatter:off*/
    try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);
        BufferedReader reader = new BufferedReader(read)) {
      /*@formatter:on*/
      strList = reader.lines().filter(StrUtil::isNotBlank).collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return strList;
  }

  public static List<String> getFileContent(MultipartFile file) {
    List<String> strList = new ArrayList<>();
    File f = multipartFileToFile(file);
    /*@formatter:off*/
    try (
        InputStreamReader read = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(read)) {
      /*@formatter:on*/
      strList = reader.lines().filter(StrUtil::isNotBlank).collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      f.delete();
    }
    return strList;
  }

  /**
   * MultipartFile 转 File
   *
   * @param file
   */
  public static File multipartFileToFile(MultipartFile file) {
    File toFile;
    if (file == null || file.getSize() <= 0) {
      return null;
    }
    try (InputStream ins = file.getInputStream()) {
      toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
      inputStreamToFile(ins, toFile);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return toFile;
  }

  /**
   * 获取流文件
   */
  private static void inputStreamToFile(InputStream ins, File file) {
    try (OutputStream os = new FileOutputStream(file)) {
      int bytesRead;
      byte[] buffer = new byte[8192];
      while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
      ins.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static <T> List<T> csv2list(File file, Class<T> tClass) {
    return csv2list(file.getAbsolutePath(), tClass);
  }

  public static <T> List<T> csv2list(String resource, Class<T> tClass) {
    CsvReader csvReader = CsvUtil.getReader();
    //使用GBK编码，否则中文出现乱码，
    //若使用Utf8 可以直接使用ResourceUtil.getUtf8Reader("test2.csv")
    BufferedReader reader = FileUtil.getReader(resource, CharsetUtil.CHARSET_GBK);
    List<T> rows = csvReader.read(reader, tClass);
    for (T bean : rows) {
      System.out.println(bean);
    }
    return rows;
  }
}
