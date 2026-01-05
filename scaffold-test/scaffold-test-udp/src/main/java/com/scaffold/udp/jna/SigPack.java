package com.scaffold.udp.jna;

import cn.hutool.core.util.NumberUtil;
import com.sun.jna.Structure;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;


/**
 * 信号列表右键存储，保存到数据库，可以按信号选择存储
 * 画图中，脉宽改名为周期
 */
@Setter
// 对应 C struct SigPack (单个识别信号及其PDW数据)
@Structure.FieldOrder({"Num", "mark", "CWFlag", "toa", "ampp", "pw", "fre", "fmin", "fmax", "fre_cent", "BW", "delt_TOA", "delt_TOA1", "angle", "angle_max", "Sig_Part"})
public class SigPack extends Structure {
    public int Num;
    //标志位     上位机不显示
    public byte[] mark;
    //连续波标志  0-否，1-是  上位机不显示
    public byte[] CWFlag;
    //到达时间 每153.75个代表1us，此处上位机显示使用时可以不按实际值计算，可用脉冲序号递增横坐标等间隔规划
    public long[] toa;
    //幅度
    public int[] ampp;
    //脉宽
    public int[] pw;
    //中频
    public int[] fre;
    //最小频率
    public int[] fmin;
    //最大频率
    public int[] fmax;
    //中心频率
    public int[] fre_cent;
    //带宽
    public int[] BW;
    //这个不用
    public int[] delt_TOA;
    //用这个时间
    public int[] delt_TOA1;
    //方位角
    public short[] angle;
    //脉冲PDW标志位，上位机不显示
    public short angle_max;
    public SigPart Sig_Part;

    public SigPack() {
        super(Structure.ALIGN_NONE);
        mark = new byte[PdwConstants.PDW_NUM_OF_PACK];
        CWFlag = new byte[PdwConstants.PDW_NUM_OF_PACK];
        toa = new long[PdwConstants.PDW_NUM_OF_PACK];
        ampp = new int[PdwConstants.PDW_NUM_OF_PACK];
        pw = new int[PdwConstants.PDW_NUM_OF_PACK];
        fre = new int[PdwConstants.PDW_NUM_OF_PACK];
        fmin = new int[PdwConstants.PDW_NUM_OF_PACK];
        fmax = new int[PdwConstants.PDW_NUM_OF_PACK];
        fre_cent = new int[PdwConstants.PDW_NUM_OF_PACK];
        BW = new int[PdwConstants.PDW_NUM_OF_PACK];
        delt_TOA = new int[PdwConstants.PDW_NUM_OF_PACK];
        delt_TOA1 = new int[PdwConstants.PDW_NUM_OF_PACK];
        angle = new short[PdwConstants.PDW_NUM_OF_PACK];
        Sig_Part = new SigPart();
    }

    public static void main(String[] args) {
        //21 4748 3647
        System.out.println(NumberUtil.decimalFormat("#.###", 12345.1234567));

    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("Num", "mark", "CWFlag", "toa", "ampp", "pw", "fre", "fmin", "fmax", "fre_cent", "BW", "delt_TOA", "delt_TOA1", "angle", "angle_max", "Sig_Part");
    }

    /*public SigPack() {
        super(Structure.ALIGN_NONE);
        // 初始化 Sig_Part
        Sig_Part = new SigPart();
    }*/

    /**
     *
     */
    @Override
    public void read() {
        super.read();
        Sig_Part.read();
    }


}