package github.polarisink.scaffold.infrastructure.asserts;

import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.Map;

/**
 * 断言类
 *
 * @Author Administrator
 * @Date 2023/3/27 14:47
 */
public class AssertUtil {

    /**
     * 创建异常
     *
     * @param errorEnum
     * @param args
     * @return
     */
    private static BaseException newException(BaseEnum errorEnum, Object... args) {
        String msg = args == null || args.length == 0 ? errorEnum.getMessage() : StrUtil.format(errorEnum.getMessage(), args);
        return new BaseException(errorEnum.getCode(), msg);
    }

    /**
     * 判断对象相关方法================================================================================================
     *
     * @param object
     */
    public static void assertNotNull(Object object, BaseEnum errorEnum, Object... args) {
        if (object == null) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertNull(Object object, BaseEnum errorEnum, Object... args) {
        if (object != null) {
            throw newException(errorEnum, args);
        }
    }


    /**
     * 判断字符串相关方法================================================================================================
     *
     * @param str object
     */

    public static void assertNotEmpty(String str, BaseEnum errorEnum, Object... args) {
        if (isEmpty(str)) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertEmpty(String str, BaseEnum errorEnum, Object... args) {
        if (!isEmpty(str)) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertNotBlank(String str, BaseEnum errorEnum, Object... args) {
        if (StrUtil.isBlank(str)) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertBlank(String str, BaseEnum errorEnum, Object... args) {
        if (StrUtil.isNotBlank(str)) {
            throw newException(errorEnum, args);
        }
    }

    private static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断集合相关方法================================================================================================
     *
     * @param collection list
     */
    public static void assertNotEmpty(Collection<?> collection, BaseEnum errorEnum, Object... args) {
        if (collection == null || collection.isEmpty()) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertEmpty(Collection<?> collection, BaseEnum errorEnum, Object... args) {
        if (collection != null && !collection.isEmpty()) {
            throw newException(errorEnum, args);
        }
    }

    /**
     * 判断数组相关方法================================================================================================
     */
    public static void assertNotEmpty(Object[] list, BaseEnum errorEnum, Object... args) {
        if (list == null || list.length == 0) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertEmpty(Object[] list, BaseEnum errorEnum, Object... args) {
        if (list != null && list.length > 0) {
            throw newException(errorEnum, args);
        }
    }

    /**
     * 判断map相关方法================================================================================================
     *
     * @param map map
     */
    public static void assertNotEmpty(Map<?, ?> map, BaseEnum errorEnum, Object... args) {
        if (map == null || map.isEmpty()) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertEmpty(Map<?, ?> map, BaseEnum errorEnum, Object... args) {
        if (map != null && !map.isEmpty()) {
            throw newException(errorEnum, args);
        }
    }

    /**
     * 判断bool表达式相关方法================================================================================================
     *
     * @param expression
     * @param errorEnum
     * @param args
     */
    public static void assertFalse(boolean expression, BaseEnum errorEnum, Object... args) {
        if (expression) {
            throw newException(errorEnum, args);
        }
    }

    public static void assertTrue(boolean expression, BaseEnum errorEnum, Object... args) {
        if (!expression) {
            throw newException(errorEnum, args);
        }
    }

}
