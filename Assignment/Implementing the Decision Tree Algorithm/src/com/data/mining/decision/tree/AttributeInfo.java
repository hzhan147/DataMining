//package com.data.mining.decision.tree;
//
//import java.util.*;
//
///**
// * POJO class that holds each attribute information
// */
//public class AttributeInfo implements Cloneable{
//    private int attributeId;
//    private final Set<Integer> uniqueAttributeValues;
//    private final Set<Integer> uniqueClasses;
//    /**
//     * {index, value}
//     */
//    private final Map<Integer,Integer> listOfIndexValue;
//    private final Map<Integer,Integer> listOfIndexClass;
//
//    public AttributeInfo(int attributeId) {
//        this.attributeId = attributeId;
//        this.uniqueAttributeValues = new HashSet<>();
//        this.uniqueClasses = new HashSet<>();
//        this.listOfIndexValue = new HashMap<>();
//        this.listOfIndexClass= new HashMap<>();
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
//    public Set<Integer> getUniqueAttributeValues() {
//        return uniqueAttributeValues;
//    }
//
//    public void addUniqueAttributeValue(Integer attributeVal, Integer rowIndex) {
//        this.uniqueAttributeValues.add(attributeVal);
//        this.listOfIndexValue.put(rowIndex,attributeVal);
//    }
//
//    public void addClass(int clazz,int rowIndex){
//        this.uniqueClasses.add(clazz);
//        this.listOfIndexClass.put(rowIndex,clazz);
//    }
//
//    public Set<Integer> getUniqueClasses() {
//        return uniqueClasses;
//    }
//
//    public Map<Integer, Integer> getListOfIndexValue() {
//        return listOfIndexValue;
//    }
//
//    public Map<Integer, Integer> getListOfIndexClass() {
//        return listOfIndexClass;
//    }
//
//    public Set<Set<Integer>> generateAttributeValuesPowerSet(){
//        return PowerSet.powerSet(getUniqueAttributeValues());
//    }
//
//    @Override
//    protected AttributeInfo clone() {
//        AttributeInfo attributeInfo = new AttributeInfo(this.getAttributeId());
//        this.getUniqueClasses().forEach(integer -> attributeInfo.getUniqueClasses().add(integer));
//        this.getUniqueAttributeValues().forEach(integer -> attributeInfo.getUniqueAttributeValues().add(integer));
//        this.getListOfIndexValue().forEach((integer, integer2) -> attributeInfo.getListOfIndexValue().put(integer,integer2));
//        this.getListOfIndexClass().forEach((integer, integer2) -> attributeInfo.getListOfIndexClass().put(integer,integer2));
//        return attributeInfo;
//    }
//}
