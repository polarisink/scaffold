package github.polarisink.dao.bean.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aries
 * @date 2022/4/29
 */

@Data
@Component
@ConfigurationProperties(prefix = "swagger")
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
  private String title;
  /**
   * 应用描述
   */
  private String description;
  /**
   * 服务地址
   */
  private String serviceUrl;
  /**
   * 版本，默认V1.0.0
   */
  private String version;
  /**
   * license
   */
  private String license;
  /**
   * licenseUrl
   */
  private String licenseUrl;

  /**
   * 作者
   */
  private String author;

  /**
   * 作者邮箱
   */
  private String email;

  /**
   *
   */
  private String url;
}
