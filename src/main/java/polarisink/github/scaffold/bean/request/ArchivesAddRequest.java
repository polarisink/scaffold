package polarisink.github.scaffold.bean.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static polarisink.github.scaffold.utils.TimeUtils.sFStr;


/**
 * 机床档案新增请求
 *
 * @author aries
 * @date 2022/5/11
 */
@Data
@ApiModel("机床档案插入实体请求")
public class ArchivesAddRequest implements Serializable {

  private static final long serialVersionUID = 1784425732671451601L;

  @ApiModelProperty("机床标识")
  private String handle;

  @ApiModelProperty("光机编号")
  private String opticalMachNum;

  @ApiModelProperty("光机落位时间")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime opticalMachLoadTime;

  @ApiModelProperty("机床编号")
  private String machNum;

  @ApiModelProperty("机床型号Id")
  private Long machModelId;

  @ApiModelProperty("出厂时间")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime factoryTime;

  @ApiModelProperty("SN码")
  private String machSn;

  @ApiModelProperty("产品名称")
  private String prodName;

  @ApiModelProperty("产品规格")
  private String prodStandard;

  @ApiModelProperty("出厂编号")
  private String serialNum;

  @ApiModelProperty("bomID")
  private Long bomId;

}
