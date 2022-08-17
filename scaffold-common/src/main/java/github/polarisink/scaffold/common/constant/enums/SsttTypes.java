package github.polarisink.scaffold.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author aries
 * @date 2022/8/10
 */
@Getter
@RequiredArgsConstructor
public enum SsttTypes implements BaseEnum {
  /*@formatter:off*/
  SPEED(0,"速度环"),
  LOCATE(1,"位置环"),
  ROUNDNESS(2,"圆度测试"),
  RIGID_TAP(3,"刚性攻丝"),
  NOTCH_FILTER(4,"陷波器"),
  GANTRY_SYNC(5,"龙门同步"),
  SPINDLE_LIFT(6,"主轴升降速"),
  INVERTER_ATTACK(7,"变频器刚攻"),
  TOOL_CHANGE_TIME(8,"换刀时间"),
  DIY(9,"自定义"),
  FULLY_CLOSED_LOOP_DIAGNOSIS(10,"全闭环诊断"),
  RASTER_DETECTION(11,"光栅检测"),
  Z_THERMAL_RRROR(12,"Z轴热误差"),
  MAJOR_THERMAL_RRROR(13,"主轴热误差"),
  FULL_CURRENT(14,"全程电流"),
  DIAGNOSTIC_RECORD(15,"诊断记录"),
  ADJUST_REPORT(16,"调机报表"),
  ;
  private final Integer type;
  private final String name;
}
