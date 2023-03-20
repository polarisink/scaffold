package github.polarisink.common.constant;


import cn.hutool.core.util.StrUtil;

/**
 * Redis缓存名
 *
 * @author lqs
 * @date 2022/3/31
 */
public class RedisConst {

    public static final String REDIS_SEP = StrUtil.COLON;

    /**
     * 项目的KEY,作为基
     */
    public static final String ASSEMBLY = "ASSEMBLY" + REDIS_SEP;
    /**
     * 不同业务的KEY
     */
    public static final String TEMPLATE = ASSEMBLY + "TEMPLATE";
    public static final String SEARCH_DIC = ASSEMBLY + "SEARCH_DIC";
    public static final String DATA_DETAIL = ASSEMBLY + "DATA_DETAIL";
    public static final String POSITION_ERROR = ASSEMBLY + "POSITION_ERROR";
    public static final String CONTROL_PARAM = ASSEMBLY + "CONTROL_PARAM";
    public static final String CONTROL_PARAM3 = ASSEMBLY + "CONTROL_PARAM3";
    public static final String HEALTH = ASSEMBLY + "HEALTH";
    public static final String ACCURACY3 = ASSEMBLY + "ACCURACY3";
    public static final String ASSEMBLY_TREE = ASSEMBLY + "ASSEMBLY_TREE";
    public static final String REMOTE_DETAIL = ASSEMBLY + "REMOTE_DETAIL";
    public static final String ORIGINAL_DETAIL = ASSEMBLY + "ORIGINAL_DETAIL";

    public static final String TOKEN = ASSEMBLY + "TOKEN";

    public static final String VERIFY_CODE = ASSEMBLY + "VERIFY_CODE";

    public static final String MENU_DETAIL = ASSEMBLY + "MENU_DETAIL";
    public static final String ROLE_DETAIL = ASSEMBLY + "ROLE_DETAIL";
    public static final String ARCHIVES_SN_LIST = ASSEMBLY + "ARCHIVES_SN_LIST";

    public static final String SSTT = ASSEMBLY + "SSTT";
    public static final String TAI_ZHENG = ASSEMBLY + "TAI_ZHENG";
    public static final String TAI_ZHENG2 = ASSEMBLY + "TAI_ZHENG2";

    private RedisConst() {

    }

}
