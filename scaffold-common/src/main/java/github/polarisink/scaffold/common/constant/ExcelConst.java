package github.polarisink.scaffold.common.constant;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

/**
 * excel导入用的部分常量
 *
 * @author lqs
 * @date 2022/3/9
 */
public class ExcelConst {
  /**
   * 环节
   */
  public static final String link = "环节";
  /**
   * 架设
   */
  public static final String erect = "架设";
  /**
   * 项目
   */
  public static final String project = "项目";

  public static final String testItems = "检测项目";

  public static final String workStep = "工步";

  public static final String process = "流程";
  public static final String pass = "通过/未通过";
  /**
   * 测量示意
   */
  public static final String measure = "测量示意";

  public static final String radio = "radio";

  public static final List<String> positive = ImmutableList.of("通过");


  /**
   * 4个公共表头
   */
  public static final List<String> ignoredKeys = Arrays.asList(link, erect, project, measure);

  private ExcelConst() {
  }
}
