package github.polarisink.common.constant;

/**
 *
 */
public class StepConst {

    public static final Long ROOT_PARENT_ID = 0L;

    /**
     * 表单或步骤所在树一级步骤的ID，如果本身就是一级步骤,则使用该虚拟值
     */
    public static final Long VIRTUAL_ROOT_ID = 0L;

    /**
     * 排名第一的orderNum为1
     */
    public static final Integer FIRST_ORDER = 1;

    /**
     * 默认表单名
     */
    public static final String DEFAULT_FORM_NAME = "默认表单名";

    /**
     * 表单默认类型为多行文本块:textarea
     */
    public static final String DEFAULT_FORM_TYPE = "textarea";

    /**
     * 用于拷贝时新副本追加的名称
     */
    public static final String COPY_SUFFIX = "-副本";

    /**
     * 远程数据键第三个字符
     */
    public static final String REMOTE_3ST_VALUE = "_";

    /**
     * selfTestTime
     */
    public static final String SELF_TEST_TIME = "自检时间";

    /**
     * 健康度数值
     */
    public static final String HEALTH_NAME = "健康度数值";
    /**
     * 健康度数值默认值
     */
    public static final String DEFAULT_V_STR = "0.000";

    public static final Double DEFAULT_V = Double.valueOf(DEFAULT_V_STR);

    public static final String CALCULATE = "计算值(mm)";

    public static final String COMPENSATE = "补偿后重复定位精度";

    public static final String START_NUM = "螺距误差补偿表起始参数号";

    public static final String COMPENSATE_POINT = "螺距误差补偿点数";


    /**
     * 几个重要模板
     */
    public static final String GEOMETRIC_ACCURACY_DETECTION = "几何精度检测";
    public static final String SPINDLE_THERMAL_ELONGATION = "主轴热伸长";
    public static final String REPEATABILITY = "重复定位精度";
    public static final String REMARK = "外部数据上传";
    public static final String SSTT_DATA = "SSTT数据上传";

    /**
     * 禁止实例
     */
    private StepConst() {

    }
}
