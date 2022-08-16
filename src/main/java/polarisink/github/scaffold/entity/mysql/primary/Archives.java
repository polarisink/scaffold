package polarisink.github.scaffold.entity.mysql.primary;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import polarisink.github.scaffold.entity.mysql.BaseJpaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

import static polarisink.github.scaffold.utils.TimeUtils.sFStr;


/**
 * 机床档案<br/>
 * mach:machine缩写
 *
 * @author aries
 * @date 2022/4/28
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "archives")
@Entity(name = "archives")
@ApiModel("机床档案")
public class Archives extends BaseJpaEntity {
  private static final long serialVersionUID = -2970421873172590326L;

  /**
   * 用handle是按照学校那边的统一要求,英文意思对不上不用管
   */
  @Alias("标识")
  @ApiModelProperty("机床标识")
  @Column(columnDefinition = "varchar(255) comment '机床标识'")
  private String handle;

  @Alias("光机编号")
  @ApiModelProperty("光机编号")
  @Column(columnDefinition = "varchar(255) comment '光机编号'")
  private String opticalMachNum;

  @Alias("光机落位时间")
  @ApiModelProperty("光机落位时间")
  @Column(columnDefinition = "datetime comment '光机落位时间'")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime opticalMachLoadTime;

  @Alias("机床编号")
  @ApiModelProperty("机床编号")
  @Column(columnDefinition = "varchar(255) comment '机床编号'")
  private String machNum;

  @ApiModelProperty("机床型号")
  @Column(columnDefinition = "bigint comment '机床型号'")
  private Long machModelId;

  @ApiModelProperty("进度")
  @Column(columnDefinition = "int comment '进度'")
  private Integer process;

  @Alias("出厂时间")
  @ApiModelProperty("出厂时间")
  @Column(columnDefinition = "datetime comment '出厂时间'")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime factoryTime;

  @Alias("出厂编号")
  @ApiModelProperty("出厂编号")
  @Column(columnDefinition = "varchar(255) comment '出厂编号'")
  private String serialNum;

  @Alias("机床SN码")
  @ApiModelProperty("SN码")
  @Column(columnDefinition = "varchar(255) comment 'SN码'")
  private String machSn;

  @Alias("产品名称")
  @ApiModelProperty("产品名称")
  @Column(columnDefinition = "varchar(255) comment '产品名称'")
  private String prodName;

  @Alias("产品规格")
  @ApiModelProperty("产品规格")
  @Column(columnDefinition = "varchar(255) comment '产品名称'")
  private String prodStandard;

  @ApiModelProperty("bomID")
  @Column(columnDefinition = "bigint comment 'bomID'")
  private Long bomId;

  @ApiModelProperty("注册状态")
  @Column(columnDefinition = "bit comment '注册状态' not null default false")
  private Boolean register;

  @ApiModelProperty("在线状态")
  private Boolean online;

  /**
   * 数据修改的时间
   */
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  @CreatedDate
  private LocalDateTime dataTime;

  /**
   * mqtt时间
   */
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  @CreatedDate
  private LocalDateTime mqttTime;


  //-----数据库不存在字段

  @Transient
  private Boolean hasHandle;

}
