package jdk;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_FORMATTER;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class Java8FilesTest {

  private static final String HOME = System.getProperty("user.home");

  public static final String SYSTEM_FILE_PATH = HOME + "/system";


  @Test
  public void resolve() {
    Path path = Paths.get(SYSTEM_FILE_PATH);
    Path dic = path.resolve("csikfmss");
    Path locate = dic.resolve(PURE_DATETIME_FORMATTER.format(LocalDateTime.now()));
    try {
      Files.createDirectory(path);
      Files.createDirectory(dic);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void list() throws IOException {
    Stream<Path> list = Files.list(Paths.get("C:", "Users", "lqsgo", "system"));
    List<String> collect = list.map(f -> f.getFileName().toString()).collect(Collectors.toList());
    System.out.println(collect);
  }

  @Test
  public void tmp() throws IOException {
    Path path = Paths.get("system", "xas");
    System.out.println(Files.createTempFile(path, "dssss", null).toFile());
  }

  @Test
  public void compareFiles() {
    String path1 = "C:\\Users\\hzsk\\Desktop\\全流程电流数据解析\\system_61484610712278477860FDFFDDC8F36DCC2ef6326e-2f69-4a0f-b2be-69d9bae007df";
    String path2 = "C:\\Users\\hzsk\\Desktop\\全流程电流数据解析\\Uploaded_FullRangeCurrent_20221031223839";
    System.out.println(compare(path1, path2));
  }

  @Test
  public void create() throws IOException {
    System.out.println(new File(SYSTEM_FILE_PATH, "qoijdqio").getAbsolutePath());
  }

  @Test
  public void newFile() {
    File file = new File("C:\\Users\\hzsk\\AppData\\Local\\Temp\\\\serializeCache");
    System.out.println(file.getAbsolutePath());
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      System.out.println(file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createDic(String pathStr) throws IOException {
    Path path = Paths.get(pathStr);
    if (Files.notExists(path)) {
      Files.createDirectory(path);
    }
  }

  private boolean compare(String path, String path2) {
    try (DataInputStream in = new DataInputStream(new FileInputStream(path)); DataInputStream in2 = new DataInputStream(
        new FileInputStream(path2))) {
      boolean flag = in.available() == in2.available();
      if (!flag) {
        return false;
      }
      for (int i = 0; i < in.available(); i++) {
        if (in.read() != in2.read()) {
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
}
