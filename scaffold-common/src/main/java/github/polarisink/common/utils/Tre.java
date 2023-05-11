package github.polarisink.common.utils;

import java.util.List;

import static java.util.Arrays.asList;

public class Tre implements ITree<Integer> {
    private Integer id;
    private Integer parentId;

    private String name;

    private List<ITree<Integer>> child;

    public Tre(Integer id, Integer parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public Tre() {
    }

    @Override
    public String toString() {
        return "Tre{id=" + id + ", parentId=" + parentId + ", name=" + name + ", child=" + child + '}';
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

    public static void main(String[] args) {
        List<ITree<Integer>> list = asList(
                new Tre(1, null, "root"),
                new Tre(2, null, "2"),
                new Tre(3, 1, "3"),
                new Tre(4, 1, "4"),
                new Tre(5, 3, "5"),
                new Tre(6, 5, "6")

        );
        //1->3->5->6
        //  >4
        //2->
        List<ITree<Integer>> iTrees = TreeUtils.tree2(list, null);
        System.out.println(iTrees);
    }
}