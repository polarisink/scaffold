package github.polarisink.common.utils.tree;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 稍微封装一下hutool的TreeUtil
 * @author aries
 * @date 2022/7/19
 * @see cn.hutool.core.lang.tree.TreeUtil
 */
@Slf4j
public class TreeUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * 通过jackson转为树形,需要自定义TreeNodeConfig
   * @param list
   * @param rootId
   * @param config
   * @param <T>
   * @param <E>
   * @return
   */
  public static <T, E> List<T> tree(List<T> list, E rootId, TreeNodeConfig config) {
    List<Tree<E>> tree = parseTree(list, rootId, config);
    return MAPPER.convertValue(tree, new TypeReference<>() {
    });
  }

  /**
   * 实现ITree接口的实体类可用
   * @param list
   * @param rootId
   * @param <T>
   * @return
   */
  public static <T> List<ITree<T>> tree(List<ITree<T>> list, T rootId) {
    Map<Optional<T>, List<ITree<T>>> map = list.stream()
        .collect(Collectors.groupingBy(tiTree -> Optional.ofNullable(tiTree.getParentId())));
    return parseChild(map, rootId);
  }


  /**
   * 转为list转为List<Tree<E>>
   * @param list
   * @param rootId
   * @param config
   * @param <T>
   * @param <E>
   * @return
   */
  private static <T, E> List<Tree<E>> parseTree(List<T> list, E rootId, TreeNodeConfig config) {
    return cn.hutool.core.lang.tree.TreeUtil.build(list, rootId, config, (node, tree) -> {
      Field[] fields = ReflectUtil.getFieldsDirectly(node.getClass(), true);
      for (Field field : fields) {
        String fieldName = field.getName();
        Object fieldValue = ReflectUtil.getFieldValue(node, field);
        tree.putExtra(fieldName, fieldValue);
      }
    });
  }

  private static <T> List<ITree<T>> parseChild(Map<Optional<T>, List<ITree<T>>> map, T rootId) {
    List<ITree<T>> list = map.get(Optional.ofNullable(rootId));
    if (list == null || list.size() == 0) {
      return Collections.emptyList();
    }
    list.forEach(t -> t.setChildren(parseChild(map, t.getId())));
    return list;
  }
}
