package com.scaffold.core.base.util;

import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ITreeTest {
    private static Tree ofTree(String id, String parentId, String name) {
        Tree tree = new Tree();
        tree.setId(id);
        tree.setParentId(parentId);
        tree.setName(name);
        return tree;
    }

    @Test
    void tree() {
        List<Tree> trees = List.of(
                ofTree("1", null, "1-1"),
                ofTree("2", null, "1-2"),
                ofTree("3", "1", "2-3"),
                ofTree("4", "1", "2-4"),
                ofTree("5", "2", "2-5"),
                ofTree("6", "2", "2-6"),
                ofTree("7", "3", "3-7"),
                ofTree("8", "3", "3-8"),
                ofTree("9", "3", "3-9"),
                ofTree("10", "4", "3-10")
        );
        List<Tree> tree = ITree.toTree(null, trees, null);
        System.out.println(tree);
    }

    @ToString
    @Data
    static class Tree implements ITree<Tree, String> {
        private String id;
        private String parentId;
        private String name;
        private List<Tree> children = new ArrayList<>();


    }

}