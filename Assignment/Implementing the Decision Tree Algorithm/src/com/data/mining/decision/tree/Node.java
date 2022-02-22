//package com.data.mining.decision.tree;
//
//import com.data.mining.decision.tree.condition.Condition;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class Node {
//    private Condition leftBranchCondition;
//    private Condition rightBranchCondition;
//    private int attributeId;
//    private Node leftNode;
//    private Node rightNode;
//    private boolean isLeaf;
//    private int leafValue;
//    private Set<Integer> attributeValues;
//
//    public Node(int attributeId) {
//        this.attributeValues = new HashSet<>();
//        this.attributeId = attributeId;
//        this.isLeaf = false;
//        this.leafValue = -1;
//    }
//
//    public Node(boolean isLeaf, int leafValue) {
//        this.attributeValues = new HashSet<>();
//        this.attributeId = -1;
//        this.isLeaf = isLeaf;
//        this.leafValue = leafValue;
//    }
//
//    public Set<Integer> getAttributeValues() {
//        return attributeValues;
//    }
//
//    public void setAttributeValues(Set<Integer> attributeValues) {
//        this.attributeValues = attributeValues;
//    }
//
//    public Condition getLeftBranchCondition() {
//        return leftBranchCondition;
//    }
//
//    public void setLeftBranchCondition(Condition leftBranchCondition) {
//        this.leftBranchCondition = leftBranchCondition;
//    }
//
//    public Condition getRightBranchCondition() {
//        return rightBranchCondition;
//    }
//
//    public int getLeafValue() {
//        return leafValue;
//    }
//
//    public void setLeafValue(int leafValue) {
//        this.leafValue = leafValue;
//    }
//
//    public void setRightBranchCondition(Condition rightBranchCondition) {
//        this.rightBranchCondition = rightBranchCondition;
//    }
//
//    public int getAttributeId() {
//        return attributeId;
//    }
//
//    public void setAttributeId(int attributeId) {
//        this.attributeId = attributeId;
//    }
//
//    public Node getLeftNode() {
//        return leftNode;
//    }
//
//    public void setLeftNode(Node leftNode) {
//        this.leftNode = leftNode;
//    }
//
//    public Node getRightNode() {
//        return rightNode;
//    }
//
//    public void setRightNode(Node rightNode) {
//        this.rightNode = rightNode;
//    }
//
//    public boolean isLeaf() {
//        return isLeaf;
//    }
//
//    public void setLeaf(boolean leaf) {
//        isLeaf = leaf;
//    }
//}
