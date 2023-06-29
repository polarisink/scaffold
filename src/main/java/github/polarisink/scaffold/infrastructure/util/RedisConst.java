package github.polarisink.scaffold.infrastructure.util;


import cn.hutool.core.util.StrUtil;

/**
 * Redis缓存名
 *
 * @author lqs
 * @date 2022/3/31
 */
public interface RedisConst {

    String REDIS_SEP = StrUtil.COLON;

    /**
     * 项目的KEY,作为基
     */
    String ASSEMBLY = "ASSEMBLY" + StrUtil.COLON;

    /**
     * 用户token
     */
    String TOKEN = ASSEMBLY + "TOKEN";

    String VERIFY_CODE = ASSEMBLY + "VERIFY_CODE";

    /**
     * 菜单
     */
    String MENU_DETAIL = ASSEMBLY + "MENU_DETAIL";
    /**
     * 角色
     */
    String ROLE_DETAIL = ASSEMBLY + "ROLE_DETAIL";

    String TEMPLATE = ASSEMBLY + "TEMPLATE";
    String ARCHIVES = ASSEMBLY + "ARCHIVES";


}
