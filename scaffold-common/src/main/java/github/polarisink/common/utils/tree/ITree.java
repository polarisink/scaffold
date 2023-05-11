package github.polarisink.common.utils.tree;

import java.util.List;

 public interface ITree<T> {
    T getId();

    T getParentId();

    void setChildren(List<ITree<T>> children);
}
