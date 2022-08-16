package polarisink.github.scaffold.entity.mysql.secondary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 远程的表,数据由远程提供,字段不规范我也没办法
 *
 * @author lqs
 * @date 2022/3/14
 */

@Data
@ToString
@Table(name = "MetaData")
@Entity(name = "MetaData")
@ApiModel("元数据")
public class MetaData implements Serializable {
  private static final long serialVersionUID = 946125969647079214L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ApiModelProperty("sn")
  private String device;

  @ApiModelProperty("备注")
  private String remark;

  @ApiModelProperty("模板id")
  private Long templateId;

  @ApiModelProperty("类型")
  private String type;

  @ApiModelProperty("时间")
  private Long time;

  @ApiModelProperty("时间戳")
  private Long timestamp;

}
