package github.polarisink.dao.entity.primary;

import static github.polarisink.common.utils.TimeUtils.sFStr;

import com.fasterxml.jackson.annotation.JsonFormat;
import github.polarisink.dao.entity.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 机床档案<br/> mach:machine缩写
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
public class Archives extends BaseJpaEntity {

  private static final long serialVersionUID = -2970421873172590326L;

  /**
   * 用handle是按照学校那边的统一要求,英文意思对不上不用管
   */

  @Column(columnDefinition = "varchar(255) comment '机床标识'")
  private String handle;


  @Column(columnDefinition = "varchar(255) comment '光机编号'")
  private String opticalMachNum;


  @Column(columnDefinition = "datetime comment '光机落位时间'")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime opticalMachLoadTime;

  @Column(columnDefinition = "varchar(255) comment '机床编号'")
  private String machNum;

  @Column(columnDefinition = "bigint comment '机床型号'")
  private Long machModelId;

  @Column(columnDefinition = "int comment '进度'")
  private Integer process;

  @Column(columnDefinition = "datetime comment '出厂时间'")
  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  private LocalDateTime factoryTime;

  @Column(columnDefinition = "varchar(255) comment '出厂编号'")
  private String serialNum;

  @Column(columnDefinition = "varchar(255) comment 'SN码'")
  private String machSn;

  @Column(columnDefinition = "varchar(255) comment '产品名称'")
  private String prodName;

  @Column(columnDefinition = "varchar(255) comment '产品名称'")
  private String prodStandard;

  @Column(columnDefinition = "bigint comment 'bomID'")
  private Long bomId;

  @Column(columnDefinition = "bit comment '注册状态'")
  private Boolean register;

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
