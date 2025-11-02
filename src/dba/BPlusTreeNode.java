package dba;

import java.util.ArrayList;
import java.util.List;

class BPlusTreeNode {
    boolean isLeaf;
    List<String> keys;
    List<BPlusTreeNode> children;
    List<String> values;
    BPlusTreeNode next;

    BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.values = new ArrayList<>();
        this.next = null;
    }
}