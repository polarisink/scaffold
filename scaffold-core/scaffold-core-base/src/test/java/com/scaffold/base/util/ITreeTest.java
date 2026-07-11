package com.scaffold.base.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ITreeTest {

    @Test
    void shouldBuildTreeWithoutComparator() {
        TestNode root = new TestNode(1L, 0L);
        TestNode child = new TestNode(2L, 1L);

        List<TestNode> tree = ITree.toTree(0L, List.of(root, child), null);

        assertThat(tree).containsExactly(root);
        assertThat(root.getChildren()).containsExactly(child);
        assertThat(child.getChildren()).isEmpty();
    }

    @Test
    void shouldReturnMutableEmptyListWhenRootMissing() {
        List<TestNode> tree = ITree.toTree(99L, List.of(new TestNode(1L, 0L)), Comparator.comparing(TestNode::getId));

        assertThat(tree).isEmpty();
        tree.add(new TestNode(2L, 0L));
        assertThat(tree).hasSize(1);
    }

    @Test
    void shouldResetChildrenBeforeRebuild() {
        TestNode root = new TestNode(1L, 0L);
        root.setChildren(new ArrayList<>(List.of(new TestNode(99L, 1L))));

        ITree.toTree(0L, List.of(root), null);

        assertThat(root.getChildren()).isEmpty();
    }

    @Test
    void shouldDetectCycles() {
        TestNode first = new TestNode(1L, 2L);
        TestNode second = new TestNode(2L, 1L);

        assertThatThrownBy(() -> ITree.toTree(1L, List.of(first, second), null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cyclic");
    }

    static class TestNode implements ITree<TestNode, Long> {
        private final Long id;
        private final Long parentId;
        private List<TestNode> children = new ArrayList<>();

        TestNode(Long id, Long parentId) {
            this.id = id;
            this.parentId = parentId;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public Long getParentId() {
            return parentId;
        }

        @Override
        public List<TestNode> getChildren() {
            return children;
        }

        @Override
        public void setChildren(List<TestNode> children) {
            this.children = children;
        }
    }
}
