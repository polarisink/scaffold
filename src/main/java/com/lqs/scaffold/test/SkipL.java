package com.lqs.scaffold.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Stack;

/**
 * @author lqs
 * @date 2022/3/23
 */
public class SkipL<K extends Comparable, V> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    Node<K, V> head;
    int highLevel;
    Random random;
    final int MAX_LEVEL = 32;

    public SkipL(K k, V v) {
        new Node<>(k, v);
    }

    private Node<K, V> search(K k) {
        Node<K, V> team = head;
        while (team != null) {
            if (team.key == k) {
                return team;
            } else if (team.right == null)//右侧没有了，只能下降
            {
                team = team.down;
            } else if (team.right.key.compareTo(k) > 0)//需要下降去寻找
            {
                team = team.down;
            } else //右侧比较小向右
            {
                team = team.right;
            }
        }
        return null;
    }

    public void delete(K key) {
        Node team = head;
        while (team != null) {
            if (team.right == null) {//右侧没有了，说明这一层找到，没有只能下降
                team = team.down;
            } else if (team.right.key == key)//找到节点，右侧即为待删除节点
            {
                team.right = team.right.right;//删除右侧节点
                team = team.down;//向下继续查找删除
            } else if (team.right.key.compareTo(key) > 0)//右侧已经不可能了，向下
            {
                team = team.down;
            } else { //节点还在右侧
                team = team.right;
            }
        }
    }

    public void add(Node<K, V> node) {
        K key = node.key;
        Node<K, V> search = search(key);
        if (search != null) {//存在该节点
            search.value = node.value;
            return;
        }
        Stack<Node<K,V>> stack = new Stack<>();
        Node<K,V> team = head;
    }
}

class Node<K extends Comparable, V> {
    K key;
    V value;
    Node<K, V> right, down;

    public Node(K k, V v) {

    }
}
