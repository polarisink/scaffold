package github.polarisink.scaffold.infrastructure.util;


import github.polarisink.scaffold.infrastructure.asserts.BaseEnum;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 枚举工具类:将枚举
 *
 * @author aries
 * @date 2022/8/10
 */
public class BaseEnumUtil {

    private static final Map<? extends Class<? extends BaseEnum>, ? extends Map<Integer, ? extends BaseEnum>> map;
    //private static final Map<? extends Class<? extends BaseEnum>, Map<Integer, ? extends List<? extends BaseEnum>>> mapList;

    static {
        Reflections reflections = new Reflections("github.polarisink.scaffold");
        Set<Class<? extends BaseEnum>> monitorClasses = reflections.getSubTypesOf(BaseEnum.class);
        map = monitorClasses.stream().collect(Collectors.toMap(m -> m, m -> Arrays.stream(m.getEnumConstants()).collect(Collectors.toMap(BaseEnum::getCode, e -> e))));
        //mapList = monitorClasses.stream().collect(Collectors.toMap(m -> m, m -> Arrays.stream(m.getEnumConstants()).collect(Collectors.groupingBy(BaseEnum::getCode))));
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEnum> T getByCode(Class<T> tClass, int code) {
        Map<Integer, ? extends BaseEnum> list = map.get(tClass);
        return list == null || list.isEmpty() ? null : (T) list.get(code);
    }

}
