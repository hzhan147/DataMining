package com.data.mining.decision.tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s;
        try {
            if ((s = br.readLine()) != null) {
                int num_train = Integer.parseInt(s);
                List<String> trainStrings = new ArrayList<>();
                for (int i = 0; i < num_train; i++) {
                    s = br.readLine();
                    if (s != null) {
                        trainStrings.add(s);
                    } else {
                        System.exit(1);
                    }
                }

                s = br.readLine();
                int num_test = Integer.parseInt(s);
                List<String> testStrings = new ArrayList<>();
                for (int i = 0; i < num_test; i++) {
                    s = br.readLine();
                    if (s != null) {
                        testStrings.add(s);
                    } else {
                        System.exit(1);
                    }
                }
                int[][] dataset = loadTrainingData(trainStrings);
                DecisionTree decisionTree = new DecisionTree(dataset);
                Node root = decisionTree.buildDecisionTree(null,null,-1, null);

                int[][] testset = loadTestingData(testStrings);
                for (int[] test : testset){
                    decisionTree.treverse(root, test);
                }
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private static int[][] loadTestingData(List<String> testStrings) {
        int[][] testset = new int[testStrings.size()][];
        for (int i = 0; i < testStrings.size(); i++) {
            String trainString = testStrings.get(i);
            String[] instanceSplit = trainString.split(" ");
            int[] row = new int[instanceSplit.length];
            for (int j = 0; j < instanceSplit.length; j++) {
                String[] attrValueSplit = instanceSplit[j].split(":");
                int attrValue = Integer.parseInt(attrValueSplit[1]);
                row[j] = attrValue;
            }
            testset[i] = row;
        }
        return testset;
    }

    private static int[][] loadTrainingData(List<String> trainStrings) {
        int[][] dataset = new int[trainStrings.size()][];
        for (int i = 0; i < trainStrings.size(); i++) {
            String trainString = trainStrings.get(i);
            String[] instanceSplit = trainString.split(" ");
            int[] row = new int[instanceSplit.length];
            int clazz = Integer.parseInt(instanceSplit[0]);
            for (int j = 1; j < instanceSplit.length; j++) {
                String[] attrValueSplit = instanceSplit[j].split(":");
                int attrValue = Integer.parseInt(attrValueSplit[1]);
                row[j - 1] = attrValue;
            }
            row[instanceSplit.length - 1] = clazz;
            dataset[i] = row;
        }
        return dataset;
    }
}

//--------------------------------------------------------------------------------//
abstract class Condition {
    protected List<Integer> baseVal;

    public Condition(Collection<Integer> baseVals) {
        this.baseVal = new ArrayList<>(baseVals);
    }

    public abstract boolean test(int targetVal);

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Condition Base Values: ");
        for (Integer integer : baseVal){
            stringBuilder.append(integer);
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
class EqualCondition extends Condition {
    public EqualCondition(Collection<Integer> baseVals) {
        super(baseVals);
    }

    @Override
    public boolean test(int targetVal) {
        return baseVal.contains(targetVal);
    }
}

class AttributeInfo implements Cloneable{
    private int attributeId;
    private final Set<Integer> uniqueAttributeValues;
    private final Set<Integer> uniqueClasses;
    /**
     * {index, value}
     */
    private final Map<Integer,Integer> listOfIndexValue;
    private final Map<Integer,Integer> listOfIndexClass;

    public AttributeInfo(int attributeId) {
        this.attributeId = attributeId;
        this.uniqueAttributeValues = new HashSet<>();
        this.uniqueClasses = new HashSet<>();
        this.listOfIndexValue = new HashMap<>();
        this.listOfIndexClass= new HashMap<>();
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public Set<Integer> getUniqueAttributeValues() {
        return uniqueAttributeValues;
    }

    public void addUniqueAttributeValue(Integer attributeVal, Integer rowIndex) {
        this.uniqueAttributeValues.add(attributeVal);
        this.listOfIndexValue.put(rowIndex,attributeVal);
    }

    public void addClass(int clazz,int rowIndex){
        this.uniqueClasses.add(clazz);
        this.listOfIndexClass.put(rowIndex,clazz);
    }

    public Set<Integer> getUniqueClasses() {
        return uniqueClasses;
    }

    public Map<Integer, Integer> getListOfIndexValue() {
        return listOfIndexValue;
    }

    public Map<Integer, Integer> getListOfIndexClass() {
        return listOfIndexClass;
    }

    public Set<Set<Integer>> generateAttributeValuesPowerSet(){
        return PowerSet.powerSet(getUniqueAttributeValues());
    }

    @Override
    protected AttributeInfo clone() {
        AttributeInfo attributeInfo = new AttributeInfo(this.getAttributeId());
        this.getUniqueClasses().forEach(integer -> attributeInfo.getUniqueClasses().add(integer));
        this.getUniqueAttributeValues().forEach(integer -> attributeInfo.getUniqueAttributeValues().add(integer));
        this.getListOfIndexValue().forEach((integer, integer2) -> attributeInfo.getListOfIndexValue().put(integer,integer2));
        this.getListOfIndexClass().forEach((integer, integer2) -> attributeInfo.getListOfIndexClass().put(integer,integer2));
        return attributeInfo;
    }
}

class DecisionTree {
    private int dataSize = 0;
    private List<AttributeInfo> attributeInfos = new ArrayList<>();
    private final int min_remaining_rows = 1000;
    /**
     * populate all dataset into attributes info holders
     * @param dataset
     */
    public DecisionTree(int[][] dataset) {
        this.dataSize = dataset.length;
        if (dataset.length > 0) {
            for (int col = 0; col < dataset[0].length-1; col++) {
                AttributeInfo attributeInfo = new AttributeInfo(col);
                for (int row = 0; row < dataset.length; row++) {
                    int currentAttributeValue = dataset[row][col];
                    attributeInfo.addUniqueAttributeValue(currentAttributeValue, row);
                    attributeInfo.addClass(dataset[row][dataset[row].length - 1],row); //add class
                }
                attributeInfos.add(attributeInfo);
            }
        }
    }

    public Node buildDecisionTree(Set<Integer> remainingAttributes, Set<Integer> remainingRows, int parentId, Set<Integer> parentValues) {
        Node node = null;
        SplitResult splitResult = null;
        if (remainingAttributes == null && remainingRows == null){ //root
            splitResult = getBestSplitIndex();
        }else if (remainingAttributes != null && remainingAttributes.size() != 0 && remainingRows.size() > min_remaining_rows){
            splitResult = getBestSplitIndex(remainingAttributes,remainingRows);
        }else if (remainingAttributes != null && remainingRows.size() <= min_remaining_rows){
            splitResult = new SplitResult();
            Map<Integer,Integer> filteredTransactions = attributeInfos.get(0).getListOfIndexClass().entrySet().stream().filter(entry -> remainingRows.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            int clazz = mostFrequent(new ArrayList<>(filteredTransactions.values()));
            splitResult.setParentIsLeaf(true,clazz);
        }
//        else if (remainingAttributes != null && remainingRows.size() == 1){
//            splitResult = new SplitResult();
//            splitResult.setParentIsLeaf(true,attributeInfos.get(0).getListOfIndexClass().get(remainingRows.iterator().next()));
//        }
        if (splitResult!= null && !Double.isNaN(splitResult.getValue())){
            if (splitResult.isParentLeaf()){
                node = new Node(splitResult.isParentLeaf(),splitResult.getParentLeafValue());
                node.setAttributeId(parentId);
                node.setAttributeValues(parentValues);
//                System.out.println("Leaf Node: |id:" + node.getAttributeId() + "|values:" + node.getAttributeValues() +"|leftCondition:" + node.getLeftBranchCondition() + "|rightCondition:" + node.getRightBranchCondition()+"|leaf value:" + node.getLeafValue() + "|");
            }else{
                node = new Node(splitResult.getAttributeId());
                node.setLeftBranchCondition(new EqualCondition(splitResult.getAttributeValues()));
                node.setRightBranchCondition(new EqualCondition(splitResult.getOtherAttributeValues()));
                node.setAttributeValues(splitResult.getAttributeValues());
//                System.out.println("Node: |id:" + node.getAttributeId() + "|values:" + node.getAttributeValues() +"|leftCondition:" + node.getLeftBranchCondition() + "|rightCondition:" + node.getRightBranchCondition()+"|");
                node.setLeftNode(buildDecisionTree(splitResult.getRemainingAttributes(),splitResult.getRemainingRows(), splitResult.getAttributeId(), splitResult.getAttributeValues()));
//                System.out.println("Else |other condition:" + node.getRightBranchCondition() + "|");
                node.setRightNode(buildDecisionTree(splitResult.getRemainingOtherAttributes(), splitResult.getRemainingOtherRows(), splitResult.getAttributeId(), splitResult.getOtherAttributeValues()));
            }
        }
        return node;
    }

    private Integer mostFrequent(List<Integer> list) {

        if (list == null || list.isEmpty())
            return null;

        Map<Integer, Integer> counterMap = new HashMap<Integer, Integer>();
        Integer maxValue = 0;
        Integer mostFrequentValue = null;

        for(Integer valueAsKey : list) {
            Integer counter = counterMap.get(valueAsKey);
            counterMap.put(valueAsKey, counter == null ? 1 : counter + 1);
            counter = counterMap.get(valueAsKey);
            if (counter > maxValue) {
                maxValue = counter;
                mostFrequentValue = valueAsKey;
            }
        }
        return mostFrequentValue;
    }

    private SplitResult getBestSplitIndex(Set<Integer> remainingAttributes, Set<Integer> remainingRows) {
        List<SplitResult> splitResults = new ArrayList<>();
        List<AttributeInfo> filteredAttributeInfos = attributeInfos.stream()
                .filter(attributeInfo -> remainingAttributes.contains(attributeInfo.getAttributeId())).collect(Collectors.toList());
        for (AttributeInfo attributeInfo: filteredAttributeInfos){
            AttributeInfo cloneAttributeInfo = attributeInfo.clone();
            cloneAttributeInfo.getListOfIndexClass().clear();
            cloneAttributeInfo.getListOfIndexValue().clear();
            cloneAttributeInfo.getListOfIndexValue().putAll(attributeInfo.getListOfIndexValue()
                    .entrySet().stream().filter(entry -> remainingRows.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            cloneAttributeInfo.getListOfIndexClass().putAll(attributeInfo.getListOfIndexClass()
                    .entrySet().stream().filter(entry -> remainingRows.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            //all classes are the same
            if (cloneAttributeInfo.getListOfIndexClass().values().stream().distinct().count() == 1){
                SplitResult splitResult = new SplitResult();
                splitResult.setParentIsLeaf(true,cloneAttributeInfo.getListOfIndexClass().values().stream().distinct().findFirst().get());
                return splitResult;
            }

            splitResults.add(getCurrentAttributeSplitIndex(cloneAttributeInfo));
        }
        splitResults.sort(SplitResult::compareTo);

        SplitResult bestSplit = splitResults.isEmpty() ? null : splitResults.get(0);
        filterRemainingAttributes(bestSplit);
        return bestSplit;
    }

    private SplitResult getBestSplitIndex() {
        List<SplitResult> splitResults = new ArrayList<>();
        for (AttributeInfo attributeInfo: attributeInfos){
            splitResults.add(getCurrentAttributeSplitIndex(attributeInfo));
        }
        splitResults.sort(SplitResult::compareTo);

        SplitResult bestSplit = splitResults.isEmpty() ? null : splitResults.get(0);
        filterRemainingAttributes(bestSplit);
        return bestSplit;
    }

    private void filterRemainingAttributes(SplitResult bestSplit) {
        if (bestSplit != null){
            for (AttributeInfo attributeInfo: attributeInfos){
                Map<Integer, Integer> filteredListOfIndexValue = attributeInfo.getListOfIndexValue().entrySet().stream()
                        .filter(entry -> bestSplit.getRemainingRows().contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (filteredListOfIndexValue.values().stream().distinct().count() == 1){
                    bestSplit.getRemainingAttributes().remove(attributeInfo.getAttributeId());
                }

                Map<Integer, Integer> filteredOtherListOfIndexValue = attributeInfo.getListOfIndexValue().entrySet().stream()
                        .filter(entry -> bestSplit.getRemainingOtherRows().contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (filteredOtherListOfIndexValue.values().stream().distinct().count() == 1){
                    bestSplit.getRemainingOtherAttributes().remove(attributeInfo.getAttributeId());
                }
            }
        }
    }

    private SplitResult getCurrentAttributeSplitIndex(AttributeInfo attributeInfo) {
        SplitResult splitResult = new SplitResult();
        Set<Set<Integer>> subsets = attributeInfo.generateAttributeValuesPowerSet();

        Map<Set<Integer>,Double> indexMap = new HashMap<>();
        for (Set<Integer> subset: subsets){
            Map<Integer,Integer> filteredIndexValue = new HashMap<>();
            Map<Integer,Integer> filteredOtherIndexValue = new HashMap<>();

            attributeInfo.getListOfIndexValue().entrySet().stream()
                    .filter(entry -> subset.contains(entry.getValue())).forEach(entry -> filteredIndexValue.put(entry.getKey(),entry.getValue()));
            attributeInfo.getListOfIndexValue().entrySet().stream()
                    .filter(entry -> !subset.contains(entry.getValue())).forEach(entry -> filteredOtherIndexValue.put(entry.getKey(),entry.getValue()));

            double rowCount = filteredIndexValue.size();
            double otherRowCount = filteredOtherIndexValue.size();
            double probSum1 = getProbabilitySum(attributeInfo,filteredIndexValue,rowCount);
            double probSum2 = getProbabilitySum(attributeInfo, filteredOtherIndexValue, otherRowCount);
            double subIndex = (rowCount/dataSize) * (1 - probSum1) + (otherRowCount/dataSize) * (1 - probSum2);
            indexMap.put(subset,subIndex);
        }
        Map<Set<Integer>,Double> sortedIndexMap = indexMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
        Iterator<Map.Entry<Set<Integer>, Double>> sortedIterator = sortedIndexMap.entrySet().iterator();
        if (sortedIterator.hasNext()){
            Map.Entry<Set<Integer>, Double> mapEntry = sortedIterator.next();
            Set<Integer> attributeValues = mapEntry.getKey();
            Set<Integer> otherValues = new HashSet<>(attributeInfo.getUniqueAttributeValues());
            otherValues.removeAll(attributeValues);
            double indexVal = mapEntry.getValue();
            splitResult.setAttributeId(attributeInfo.getAttributeId());
            splitResult.setAttributeValues(attributeValues);
            splitResult.setOtherAttributeValues(otherValues);
            splitResult.setValue(indexVal);

            Map<Integer,Integer> filteredIndexValue = new HashMap<>();
            Map<Integer,Integer> filteredOtherIndexValue = new HashMap<>();

            attributeInfo.getListOfIndexValue().entrySet().stream()
                    .filter(entry -> attributeValues.contains(entry.getValue())).forEach(entry -> filteredIndexValue.put(entry.getKey(),entry.getValue()));
            attributeInfo.getListOfIndexValue().entrySet().stream()
                    .filter(entry -> !attributeValues.contains(entry.getValue())).forEach(entry -> filteredOtherIndexValue.put(entry.getKey(),entry.getValue()));

            splitResult.setRemainingRows(filteredIndexValue.keySet());
            splitResult.setRemainingOtherRows(filteredOtherIndexValue.keySet());
            attributeInfos.forEach(attributeInfo1 -> splitResult.getRemainingAttributes().add(attributeInfo1.getAttributeId()));
            attributeInfos.forEach(attributeInfo1 -> splitResult.getRemainingOtherAttributes().add(attributeInfo1.getAttributeId()));
        }
        return splitResult;
    }

    private double getProbabilitySum(AttributeInfo attributeInfo, Map<Integer, Integer> filteredIndexValue, double rowCount) {
        double prob = 0.0;
        for (int clazz: attributeInfo.getUniqueClasses()){
            final double[] clazzCount = {0};
            filteredIndexValue.forEach((index, value) -> {
                if (attributeInfo.getListOfIndexClass().get(index).equals(clazz)){
                    clazzCount[0]++;
                }
            });
            prob += Math.pow((clazzCount[0] /rowCount),2);
        }
        return prob;
    }

    public void treverse(Node root, int[] ints) {
        while (!root.isLeaf()){
            if (root.getLeftBranchCondition().test(ints[root.getAttributeId()])) {
                treverse(root.getLeftNode(), ints);
                break;
            }else if (root.getRightBranchCondition().test(ints[root.getAttributeId()])){
                treverse(root.getRightNode(),ints);
                break;
            }
        }
        if (root.isLeaf()) {
            System.out.println(root.getLeafValue());
        }
    }
}

class Node {
    private Condition leftBranchCondition;
    private Condition rightBranchCondition;
    private int attributeId;
    private Node leftNode;
    private Node rightNode;
    private boolean isLeaf;
    private int leafValue;
    private Set<Integer> attributeValues;

    public Node(int attributeId) {
        this.attributeValues = new HashSet<>();
        this.attributeId = attributeId;
        this.isLeaf = false;
        this.leafValue = -1;
    }

    public Node(boolean isLeaf, int leafValue) {
        this.attributeValues = new HashSet<>();
        this.attributeId = -1;
        this.isLeaf = isLeaf;
        this.leafValue = leafValue;
    }

    public Set<Integer> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<Integer> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public Condition getLeftBranchCondition() {
        return leftBranchCondition;
    }

    public void setLeftBranchCondition(Condition leftBranchCondition) {
        this.leftBranchCondition = leftBranchCondition;
    }

    public Condition getRightBranchCondition() {
        return rightBranchCondition;
    }

    public int getLeafValue() {
        return leafValue;
    }

    public void setLeafValue(int leafValue) {
        this.leafValue = leafValue;
    }

    public void setRightBranchCondition(Condition rightBranchCondition) {
        this.rightBranchCondition = rightBranchCondition;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}

class PowerSet {
    public static Set<Set<Integer>> powerSet(Set<Integer> ints) {
        // convert set to a list
        List<Integer> S = new ArrayList<>(ints);

        // N stores total number of subsets
        long N = (long) Math.pow(2, S.size());

        // Set to store subsets
        Set<Set<Integer>> result = new HashSet<>();

        // generate each subset one by one
        for (int i = 0; i < N; i++) {
            Set<Integer> set = new HashSet<>();

            // check every bit of i
            for (int j = 0; j < S.size(); j++) {
                // if j'th bit of i is set, add S.get(j) to current set
                if ((i & (1 << j)) != 0)
                    set.add(S.get(j));
            }
            result.add(set);
        }

        result.removeIf(integers -> integers.isEmpty() || integers.size() == ints.size());
        return result;
    }
}

class SplitResult implements Comparable<SplitResult>{
    private Set<Integer> remainingAttributes;
    private Set<Integer> remainingOtherAttributes;
    private Set<Integer> remainingRows;
    private Set<Integer> remainingOtherRows;
    private double value;
    private int attributeId;
    private Set<Integer> attributeValues;
    private Set<Integer> otherAttributeValues;
    private boolean parentLeaf;
    private int parentLeafValue;

    public SplitResult() {
        this.remainingAttributes = new HashSet<>();
        this.remainingOtherAttributes = new HashSet<>();
        this.value = -1;
        this.attributeId = -1;
        this.attributeValues = new HashSet<>();
        this.otherAttributeValues = new HashSet<>();
        this.remainingRows = new HashSet<>();
        this.remainingOtherRows = new HashSet<>();
        this.parentLeaf = false;
        this.parentLeafValue = -1;
    }

    public void setParentLeaf(boolean parentLeaf) {
        this.parentLeaf = parentLeaf;
    }

    public boolean isParentLeaf() {
        return parentLeaf;
    }

    public void setParentIsLeaf(boolean parentIsLeaf, int leafValue) {
        this.parentLeaf = parentIsLeaf;
        this.parentLeafValue = leafValue;
    }

    public int getParentLeafValue() {
        return parentLeafValue;
    }

    public Set<Integer> getRemainingAttributes() {
        return remainingAttributes;
    }

    public void setRemainingAttributes(Set<Integer> remainingAttributes) {
        this.remainingAttributes = remainingAttributes;
    }

    public Set<Integer> getOtherAttributeValues() {
        return otherAttributeValues;
    }

    public void setOtherAttributeValues(Set<Integer> otherAttributeValues) {
        this.otherAttributeValues = otherAttributeValues;
    }

    public Set<Integer> getRemainingOtherAttributes() {
        return remainingOtherAttributes;
    }

    public void setRemainingOtherAttributes(Set<Integer> remainingOtherAttributes) {
        this.remainingOtherAttributes = remainingOtherAttributes;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Set<Integer> getRemainingRows() {
        return remainingRows;
    }

    public void setRemainingRows(Set<Integer> remainingRows) {
        this.remainingRows = remainingRows;
    }

    public Set<Integer> getRemainingOtherRows() {
        return remainingOtherRows;
    }

    public void setRemainingOtherRows(Set<Integer> remainingOtherRows) {
        this.remainingOtherRows = remainingOtherRows;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public Set<Integer> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<Integer> attributeValues) {
        this.attributeValues = attributeValues;
    }

    @Override
    public int compareTo(SplitResult o) {
        if (this.value == o.getValue()){
            return 0;
        }else if (this.value > o.getValue()){
            return 1;
        }else {
            return -1;
        }
    }
}