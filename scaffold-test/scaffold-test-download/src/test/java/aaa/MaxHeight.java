package aaa;

public class MaxHeight {

    public static void main(String[] args) {

    }

    public static int getHeight(Node node) {
        if (node == null) return 0;
        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    static class Node {
        String name;
        Node left, right;
    }
}
