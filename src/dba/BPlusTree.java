package dba;

import java.util.ArrayList;

public class BPlusTree {
    private final int order; // max number of keys per node
    private BPlusTreeNode root;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusTreeNode(true);
    }

    // Search
    public String search(String key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareToIgnoreCase(node.keys.get(i)) >= 0)
                i++;
            node = node.children.get(i);
        }

        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i).equalsIgnoreCase(key))
                return node.values.get(i);
        }
        return null;
    }

    // Insert
    public void insert(String key, String value) {
        BPlusTreeNode r = root;
        if (r.keys.size() == order - 1) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.children.add(r);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
    }

    private void insertNonFull(BPlusTreeNode node, String key, String value) {
        if (node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareToIgnoreCase(node.keys.get(i)) > 0)
                i++;
            node.keys.add(i, key);
            node.values.add(i, value);
        } else {
            int i = 0;
            while (i < node.keys.size() && key.compareToIgnoreCase(node.keys.get(i)) >= 0)
                i++;
            BPlusTreeNode child = node.children.get(i);

            if (child.keys.size() == order - 1) {
                splitChild(node, i);
                if (key.compareToIgnoreCase(node.keys.get(i)) >= 0)
                    i++;
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(BPlusTreeNode parent, int index) {
        BPlusTreeNode fullChild = parent.children.get(index);
        int mid = order / 2;

        BPlusTreeNode newNode = new BPlusTreeNode(fullChild.isLeaf);

        // Split logic for leaf node
        if (fullChild.isLeaf) {
            newNode.keys.addAll(fullChild.keys.subList(mid, fullChild.keys.size()));
            newNode.values.addAll(fullChild.values.subList(mid, fullChild.values.size()));

            // Shrink original node
            fullChild.keys = new ArrayList<>(fullChild.keys.subList(0, mid));
            fullChild.values = new ArrayList<>(fullChild.values.subList(0, mid));

            // Connect leaf nodes
            newNode.next = fullChild.next;
            fullChild.next = newNode;

            // Insert key to parent
            parent.keys.add(index, newNode.keys.get(0));
            parent.children.add(index + 1, newNode);
        } else {
            // For internal node
            String middleKey = fullChild.keys.get(mid);

            newNode.keys.addAll(fullChild.keys.subList(mid + 1, fullChild.keys.size()));
            newNode.children.addAll(fullChild.children.subList(mid + 1, fullChild.children.size()));

            // Shrink the original node
            fullChild.keys = new ArrayList<>(fullChild.keys.subList(0, mid));
            fullChild.children = new ArrayList<>(fullChild.children.subList(0, mid + 1));

            parent.keys.add(index, middleKey);
            parent.children.add(index + 1, newNode);
        }
    }

    // Display all
    public void display() {
        BPlusTreeNode node = root;
        while (!node.isLeaf) node = node.children.get(0);

        while (node != null) {
            for (int i = 0; i < node.keys.size(); i++)
                System.out.println(node.keys.get(i) + " → " + node.values.get(i));
            node = node.next;
        }
    }
    
    public void delete(String key) {
        if (root == null) return; // empty tree

        deleteRecursive(root, key);

        // Adjust root if it became empty
        if (!root.isLeaf && root.keys.size() == 0 && root.children.size() > 0) {
            root = root.children.get(0);
        }

        // If root is a leaf and empty, make tree empty
        if (root.isLeaf && root.keys.size() == 0) {
            root = null;
        }
    }

    private boolean deleteRecursive(BPlusTreeNode node, String key) {
        if (node.isLeaf) {
            for (int i = 0; i < node.keys.size(); i++) {
                if (node.keys.get(i).equalsIgnoreCase(key)) {
                    node.keys.remove(i);
                    node.values.remove(i);
                    return true;
                }
            }
            return false; // not found
        } else {
            int i = 0;
            while (i < node.keys.size() && key.compareToIgnoreCase(node.keys.get(i)) >= 0)
                i++;
            boolean deleted = deleteRecursive(node.children.get(i), key);

            // Update parent key if necessary
            if (deleted) {
                if (i < node.keys.size() && node.children.get(i + 1).keys.size() > 0) {
                    node.keys.set(i, node.children.get(i + 1).keys.get(0));
                }
            }
            return deleted;
        }
    }
    
    public BPlusTreeNode getRoot() {
        return root;
    }


}