package com.scaffold.biz.vo;

import org.bouncycastle.crypto.prng.drbg.CTRSP800DRBG;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;

public interface MsgType {
    // 常规命令反馈
    int REGULAR_COMMAND_FEEDBACK_0001 = 0xACBC0001;
    // 设备状态
    int DEVICE_STATUS_0101 = 0xACBC0101;
    // PDW信息
    int PDW_INFO_0202 = 0xACBC0202;
    //天线扫描周期测量相关
    int ANTENNA_SCAN_CYCLE_MEASURE_0303 = 0xACBC0303;
    //多普勒频移测试专用命令
    int DOPPLER_SHIFT_TEST_ABCD = 0xACBCABCD;
    //输出开关
    int OUTPUT_SWITCH_A5B5 = 0xACBCA5B5;
    //综合参数
    int COMPREHENSIVE_PARAM_D5C5 = 0xACBCD5C5;
    //欺骗干扰
    int FALSIFICATION_INTERFERENCE_A6B6 = 0xACBCA6B6;
    //天线方位角度命令下发
    int ANTENNA_DIRECTION_ANGLE_D1C1 = 0xACBCD1C1;
    //密集假目标
    int DENSE_FAKE_TARGET_D5C5 = 0xACBCD5C5;
    //相位编码数据表
    int PHASE_ENCODING_DATA_TABLE_0508 = 0xACBC0508;
    //重频抖动
    int REPEAT_SHOCK_0523 = 0xACBCD523;
    //打开样本加载通道
    int OPEN_SAMPLE_LOADING_CHANNEL_AA06 = 0xACBCAA06;
    //信号回放功能数据加载（第一包）
    int SIGNAL_REPLAY_FUNCTION_DATA_LOADING_FIRST_0601 = 0xACBC0601;
    //信号回放功能数据加载（后续包）
    int SIGNAL_REPLAY_FUNCTION_DATA_LOADING_FLOWUP_0602 = 0xACBC0601;
    //杂波数据申请
    int MISCELLANEOUS_WAVE_DATA_APPLICATION_AA07 = 0xACBCAA07;
    //杂波数据加载
    int MISCELLANEOUS_WAVE_DATA_LOADING_AA08 = 0xACBCAA08;
    //射频命令安全
    int RF_COMMAND_SAFETY_AB01 = 0xADBDAB01;
    //射频命令回传
    int RF_COMMAND_RETURN_AB02 = 0xADBDAB02;
    //射频命令下发
    int RF_COMMAND_ISSUE_AC01 = 0xADBDAC01;

}
