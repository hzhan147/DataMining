//package com.data.mining.decision.tree.condition;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public abstract class Condition {
//    protected List<Integer> baseVal;
//
//    public Condition(Collection<Integer> baseVals) {
//        this.baseVal = new ArrayList<>(baseVals);
//    }
//
//    public abstract boolean test(int targetVal);
//
//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Condition Base Values: ");
//        for (Integer integer : baseVal){
//            stringBuilder.append(integer);
//            stringBuilder.append(",");
//        }
//        return stringBuilder.toString();
//    }
//}
