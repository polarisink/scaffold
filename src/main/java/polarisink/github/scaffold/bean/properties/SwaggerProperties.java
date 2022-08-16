package polarisink.github.scaffold.bean.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aries
 * @date 2022/4/29
 */

@Data
@Component
@ConfigurationProperties(prefix = "hzncc.web.swagger")
public class SwaggerProperties {
  /**
   * 是否swagger3启用，默认不启用
   */
  private Boolean enable = false;
  /**
   * 扫描包路径，可以不指定，系统会通过自动扫描{@link io.swagger.v3.oas.annotations.Operation}
   */
  private String basePackage;
  /**
   * 标题
   */
  private String title = "四位一体接口";
  /**
   * 应用描述
   */
  private String description = "华中数控四位一体项目接口文档";
  /**
   * 服务地址
   */
  private String serviceUrl = "10.10.33.69";
  /**
   * 版本，默认V1.0.0
   */
  private String version = "V1.0.0";
  /**
   * license
   */
  private String license = "";
  /**
   * licenseUrl
   */
  private String licenseUrl = "";

  /**
   * 作者
   */
  private String author = "Aries";

  /**
   * 作者邮箱
   */
  private String email = "polarisink@163.com";

  /**
   *
   */
  private String url = "";
}
