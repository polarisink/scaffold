package github.polarisink.common.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aries
 * @date 2022/8/8
 */
@Getter
@RequiredArgsConstructor
@ApiModel("操作类型")
public enum OperateEnums implements BaseEnum {
  /*@formatter:off*/
  @ApiModelProperty("sstt文件上传")
  SSTT(1, "sstt文件上传"),
  @ApiModelProperty("excel文件上传")
  EXCEL(2, "excel文件上传"),
  @ApiModelProperty("系统数据上传")
  SYSTEM(3, "系统数据上传"),
  @ApiModelProperty("mqtt错误日志")
  MQTT(4, "mqtt错误日志"),
  @ApiModelProperty("默认")
  DEFAULT(0, "默认");
  /*@formatter:on*/

  private static final Map<Integer, OperateEnums> CODE_MAP = new HashMap<>();

  static {
    for (OperateEnums value : values()) {
      CODE_MAP.put(value.type, value);
    }
  }

  private final Integer type;
  private final String name;

  public static OperateEnums getByCode(Integer code) {
    return CODE_MAP.get(code);
  }
}
