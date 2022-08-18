package github.polarisink.dao.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

import static github.polarisink.common.utils.TimeUtils.sFStr;


/**
 * 机床档案新增请求
 *
 * @author aries
 * @date 2022/5/11
 */
@Data
public class ArchivesAddRequest implements Serializable {

  private static final long serialVersionUID = 1784425732671451601L;

  private String handle;

  private String opticalMachNum;

  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime opticalMachLoadTime;

  private String machNum;

  private Long machModelId;

  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime factoryTime;

  private String machSn;

  private String prodName;

  private String prodStandard;

  private String serialNum;

  private Long bomId;

}
