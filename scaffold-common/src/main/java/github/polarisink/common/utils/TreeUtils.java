package github.polarisink.common.utils;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 稍微封装一下hutool的TreeUtil
 *
 * @author aries
 * @date 2022/7/19
 * @see TreeUtil
 */
@Slf4j
public class TreeUtils {

    private static final ObjectMapper MAPPER = JacksonUtil.mapper;

    /**
     * 通过jackson转为树形List<T>
     *
     * @param list
     * @param rootId
     * @param config
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<T> treeListByJackson(List<T> list, E rootId, TreeNodeConfig config) {
        List<Tree<E>> tree = tree(list, rootId, config);
        return MAPPER.convertValue(tree, new TypeReference<>() {
        });
    }

    /**
     * 通过hutool转list<T> hutool#toJsonStr默认会忽略空值,<a href="https://gitee.com/dromara/hutool/issues/I48H5L">在这里解决</a>
     *
     * @param list
     * @param rootId
     * @param config
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<T> treeListByHutool(List<T> list, E rootId, TreeNodeConfig config) {
        //不忽略空值
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
        return JSONUtil.toBean(JSONUtil.toJsonStr(tree(list, rootId, config), jsonConfig),
                new cn.hutool.core.lang.TypeReference<>() {
                }, true);
    }


    /**
     * 转为list转为List<Tree<E>>
     *
     * @param list
     * @param rootId
     * @param config
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<Tree<E>> tree(List<T> list, E rootId, TreeNodeConfig config) {
        return TreeUtil.build(list, rootId, config, (node, tree) -> {
            Field[] fields = ReflectUtil.getFieldsDirectly(node.getClass(), true);
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = ReflectUtil.getFieldValue(node, field);
                tree.putExtra(fieldName, fieldValue);
            }
        });
    }


}
