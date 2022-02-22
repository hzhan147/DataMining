//package com.data.mining.decision.tree;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class SplitResult implements Comparable<SplitResult>{
//    private Set<Integer> remainingAttributes;
//    private Set<Integer> remainingOtherAttributes;
//    private Set<Integer> remainingRows;
//    private Set<Integer> remainingOtherRows;
//    private double value;
//    private int attributeId;
//    private Set<Integer> attributeValues;
//    private Set<Integer> otherAttributeValues;
//    private boolean parentLeaf;
//    private int parentLeafValue;
//
//    public SplitResult() {
//        this.remainingAttributes = new HashSet<>();
//        this.remainingOtherAttributes = new HashSet<>();
//        this.value = -1;
//        this.attributeId = -1;
//        this.attributeValues = new HashSet<>();
//        this.otherAttributeValues = new HashSet<>();
//        this.remainingRows = new HashSet<>();
//        this.remainingOtherRows = new HashSet<>();
//        this.parentLeaf = false;
//        this.parentLeafValue = -1;
//    }
//
//    public void setParentLeaf(boolean parentLeaf) {
//        this.parentLeaf = parentLeaf;
//    }
//
//    public boolean isParentLeaf() {
//        return parentLeaf;
//    }
//
//    public void setParentIsLeaf(boolean parentIsLeaf, int leafValue) {
//        this.parentLeaf = parentIsLeaf;
//        this.parentLeafValue = leafValue;
//    }
//
//    public int getParentLeafValue() {
//        return parentLeafValue;
//    }
//
//    public Set<Integer> getRemainingAttributes() {
//        return remainingAttributes;
//    }
//
//    public void setRemainingAttributes(Set<Integer> remainingAttributes) {
//        this.remainingAttributes = remainingAttributes;
//    }
//
//    public Set<Integer> getOtherAttributeValues() {
//        return otherAttributeValues;
//    }
//
//    public void setOtherAttributeValues(Set<Integer> otherAttributeValues) {
//        this.otherAttributeValues = otherAttributeValues;
//    }
//
//    public Set<Integer> getRemainingOtherAttributes() {
//        return remainingOtherAttributes;
//    }
//
//    public void setRemainingOtherAttributes(Set<Integer> remainingOtherAttributes) {
//        this.remainingOtherAttributes = remainingOtherAttributes;
//    }
//
//    public double getValue() {
//        return value;
//    }
//
//    public void setValue(double value) {
//        this.value = value;
//    }
//
//    public Set<Integer> getRemainingRows() {
//        return remainingRows;
//    }
//
//    public void setRemainingRows(Set<Integer> remainingRows) {
//        this.remainingRows = remainingRows;
//    }
//
//    public Set<Integer> getRemainingOtherRows() {
//        return remainingOtherRows;
//    }
//
//    public void setRemainingOtherRows(Set<Integer> remainingOtherRows) {
//        this.remainingOtherRows = remainingOtherRows;
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
//    public Set<Integer> getAttributeValues() {
//        return attributeValues;
//    }
//
//    public void setAttributeValues(Set<Integer> attributeValues) {
//        this.attributeValues = attributeValues;
//    }
//
//    @Override
//    public int compareTo(SplitResult o) {
//        if (this.value == o.getValue()){
//            return 0;
//        }else if (this.value > o.getValue()){
//            return 1;
//        }else {
//            return -1;
//        }
//    }
//}
