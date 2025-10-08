package com.scaffold.biz.vo;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 综合参数
 */
@Data
public class ComprehensiveParam implements IBytes{
    private MsgHead head;
    //通道选择
    private byte channel;
    //一级干扰类型
    private byte interferenceType;
    //二级干扰类型
    private byte interferenceType2;
    //备用
    private byte reserve;
    //门限值
    private int thresholdValue;
    //功率衰减(db)
    private int powerAttenuation;
    //时长脉冲数
    private int durationPulseNumber;
    //方位转速
    private int azimuthSpeed;
    //多普勒调制速率
    private int dopplerModulationRate;
    //压制噪声调制速率
    private int suppressesNoiseModulationRate;


    @Override
    public ByteBuf toBuf() {
        return null;
    }
}
