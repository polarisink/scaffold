package github.polarisink.common.utils.tree;

import static java.util.Arrays.asList;

import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * @author lqs
 */
public class TreeEntity implements ITree<Integer> {

  private Integer id;
  private Integer parentId;

  private String name;

  private List<ITree<Integer>> child;

  public TreeEntity(Integer id, Integer parentId, String name) {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
  }


  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public Integer getParentId() {
    return parentId;
  }

  @Override
  public void setChildren(List<ITree<Integer>> children) {
    child = children;
  }

  public static void main(String[] args) throws JsonProcessingException {
    List<ITree<Integer>> list = asList(
        new TreeEntity(1, null, "root"),
        new TreeEntity(2, null, "2"),
        new TreeEntity(3, 1, "3"),
        new TreeEntity(4, 1, "4"),
        new TreeEntity(5, 3, "5"),
        new TreeEntity(6, 5, "6"),
        new TreeEntity(7, 6, "7"),
        new TreeEntity(8, 7, "8"),
        new TreeEntity(9, 8, "9"),
        new TreeEntity(10, 6, "10")

    );
    ObjectMapper mapper = new ObjectMapper();
    long t1 = System.currentTimeMillis();
    List<ITree<Integer>> iTrees = TreeUtil.tree(list, null);
    long t2 = System.currentTimeMillis();
    TreeNodeConfig config = new TreeNodeConfig();
    config.setIdKey("id");
    config.setChildrenKey("child");
    config.setNameKey("name");
    config.setParentIdKey("parentId");
    List<ITree<Integer>> iTrees2 = TreeUtil.tree(list, null, config);
    long t3 = System.currentTimeMillis();
    System.out.println(mapper.writeValueAsString(iTrees));
    System.out.println(mapper.writeValueAsString(iTrees2));
    long time1 = t2 - t1;
    long time2 = t3 - t2;
    long total = t1 + t2;
    System.out.println(time1 * 1.0 / total);
    System.out.println(time2 * 1.0 / total);
  }
}