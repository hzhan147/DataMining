//package com.data.mining.decision.tree;
//
//import com.data.mining.decision.tree.condition.Condition;
//import com.data.mining.decision.tree.condition.EqualCondition;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class DecisionTree {
//    private int dataSize = 0;
//    private List<AttributeInfo> attributeInfos = new ArrayList<>();
//
//    /**
//     * populate all dataset into attributes info holders
//     * @param dataset
//     */
//    public DecisionTree(int[][] dataset) {
//        this.dataSize = dataset.length;
//        if (dataset.length > 0) {
//            for (int col = 0; col < dataset[0].length-1; col++) {
//                AttributeInfo attributeInfo = new AttributeInfo(col);
//                for (int row = 0; row < dataset.length; row++) {
//                    int currentAttributeValue = dataset[row][col];
//                    attributeInfo.addUniqueAttributeValue(currentAttributeValue, row);
//                    attributeInfo.addClass(dataset[row][dataset[row].length - 1],row); //add class
//                }
//                attributeInfos.add(attributeInfo);
//            }
//        }
//    }
//
//    public Node buildDecisionTree(Set<Integer> remainingAttributes, Set<Integer> remainingRows, int parentId, Set<Integer> parentValues) {
//        Node node = null;
//        SplitResult splitResult = null;
//        if (remainingAttributes == null && remainingRows == null){ //root
//            splitResult = getBestSplitIndex();
//        }else if (remainingAttributes != null && remainingAttributes.size() != 0){
//            splitResult = getBestSplitIndex(remainingAttributes,remainingRows);
//        }else if (remainingAttributes != null && remainingRows.size() == 1){
//            splitResult = new SplitResult();
//            splitResult.setParentIsLeaf(true,attributeInfos.get(0).getListOfIndexClass().get(remainingRows.iterator().next()));
//        }
//        if (splitResult!= null && !Double.isNaN(splitResult.getValue())){
//            if (splitResult.isParentLeaf()){
//                node = new Node(splitResult.isParentLeaf(),splitResult.getParentLeafValue());
//                node.setAttributeId(parentId);
//                node.setAttributeValues(parentValues);
//                System.out.println("Leaf Node: |id:" + node.getAttributeId() + "|values:" + node.getAttributeValues() +"|leftCondition:" + node.getLeftBranchCondition() + "|rightCondition:" + node.getRightBranchCondition()+"|leaf value:" + node.getLeafValue() + "|");
//            }else{
//                node = new Node(splitResult.getAttributeId());
//                node.setLeftBranchCondition(new EqualCondition(splitResult.getAttributeValues()));
//                node.setRightBranchCondition(new EqualCondition(splitResult.getOtherAttributeValues()));
//                node.setAttributeValues(splitResult.getAttributeValues());
//                System.out.println("Node: |id:" + node.getAttributeId() + "|values:" + node.getAttributeValues() +"|leftCondition:" + node.getLeftBranchCondition() + "|rightCondition:" + node.getRightBranchCondition()+"|");
//                node.setLeftNode(buildDecisionTree(splitResult.getRemainingAttributes(),splitResult.getRemainingRows(), splitResult.getAttributeId(), splitResult.getAttributeValues()));
//                System.out.println("Else |other condition:" + node.getRightBranchCondition() + "|");
//                node.setRightNode(buildDecisionTree(splitResult.getRemainingOtherAttributes(), splitResult.getRemainingOtherRows(), splitResult.getAttributeId(), splitResult.getOtherAttributeValues()));
//            }
//        }
//        return node;
//    }
//
//    private SplitResult getBestSplitIndex(Set<Integer> remainingAttributes, Set<Integer> remainingRows) {
//        List<SplitResult> splitResults = new ArrayList<>();
//        List<AttributeInfo> filteredAttributeInfos = attributeInfos.stream()
//                .filter(attributeInfo -> remainingAttributes.contains(attributeInfo.getAttributeId())).collect(Collectors.toList());
//        for (AttributeInfo attributeInfo: filteredAttributeInfos){
//            AttributeInfo cloneAttributeInfo = attributeInfo.clone();
//            cloneAttributeInfo.getListOfIndexClass().clear();
//            cloneAttributeInfo.getListOfIndexValue().clear();
//            cloneAttributeInfo.getListOfIndexValue().putAll(attributeInfo.getListOfIndexValue()
//                    .entrySet().stream().filter(entry -> remainingRows.contains(entry.getKey()))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
//            cloneAttributeInfo.getListOfIndexClass().putAll(attributeInfo.getListOfIndexClass()
//                    .entrySet().stream().filter(entry -> remainingRows.contains(entry.getKey()))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
//
//            //all classes are the same
//            if (cloneAttributeInfo.getListOfIndexClass().values().stream().distinct().count() == 1){
//                SplitResult splitResult = new SplitResult();
//                splitResult.setParentIsLeaf(true,cloneAttributeInfo.getListOfIndexClass().values().stream().distinct().findFirst().get());
//                return splitResult;
//            }
//
//            splitResults.add(getCurrentAttributeSplitIndex(cloneAttributeInfo));
//        }
//        splitResults.sort(SplitResult::compareTo);
//
//        SplitResult bestSplit = splitResults.isEmpty() ? null : splitResults.get(0);
//        filterRemainingAttributes(bestSplit);
//        return bestSplit;
//    }
//
//    private SplitResult getBestSplitIndex() {
//        List<SplitResult> splitResults = new ArrayList<>();
//        for (AttributeInfo attributeInfo: attributeInfos){
//            splitResults.add(getCurrentAttributeSplitIndex(attributeInfo));
//        }
//        splitResults.sort(SplitResult::compareTo);
//
//        SplitResult bestSplit = splitResults.isEmpty() ? null : splitResults.get(0);
//        filterRemainingAttributes(bestSplit);
//        return bestSplit;
//    }
//
//    private void filterRemainingAttributes(SplitResult bestSplit) {
//        if (bestSplit != null){
//            for (AttributeInfo attributeInfo: attributeInfos){
//                Map<Integer, Integer> filteredListOfIndexValue = attributeInfo.getListOfIndexValue().entrySet().stream()
//                        .filter(entry -> bestSplit.getRemainingRows().contains(entry.getKey()))
//                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//                if (filteredListOfIndexValue.values().stream().distinct().count() == 1){
//                    bestSplit.getRemainingAttributes().remove(attributeInfo.getAttributeId());
//                }
//
//                Map<Integer, Integer> filteredOtherListOfIndexValue = attributeInfo.getListOfIndexValue().entrySet().stream()
//                        .filter(entry -> bestSplit.getRemainingOtherRows().contains(entry.getKey()))
//                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//                if (filteredOtherListOfIndexValue.values().stream().distinct().count() == 1){
//                    bestSplit.getRemainingOtherAttributes().remove(attributeInfo.getAttributeId());
//                }
//            }
//        }
//    }
//
//    private SplitResult getCurrentAttributeSplitIndex(AttributeInfo attributeInfo) {
//        SplitResult splitResult = new SplitResult();
//        Set<Set<Integer>> subsets = attributeInfo.generateAttributeValuesPowerSet();
//
//        Map<Set<Integer>,Double> indexMap = new HashMap<>();
//        for (Set<Integer> subset: subsets){
//            Map<Integer,Integer> filteredIndexValue = new HashMap<>();
//            Map<Integer,Integer> filteredOtherIndexValue = new HashMap<>();
//
//            attributeInfo.getListOfIndexValue().entrySet().stream()
//                    .filter(entry -> subset.contains(entry.getValue())).forEach(entry -> filteredIndexValue.put(entry.getKey(),entry.getValue()));
//            attributeInfo.getListOfIndexValue().entrySet().stream()
//                    .filter(entry -> !subset.contains(entry.getValue())).forEach(entry -> filteredOtherIndexValue.put(entry.getKey(),entry.getValue()));
//
//            double rowCount = filteredIndexValue.size();
//            double otherRowCount = filteredOtherIndexValue.size();
//            double probSum1 = getProbabilitySum(attributeInfo,filteredIndexValue,rowCount);
//            double probSum2 = getProbabilitySum(attributeInfo, filteredOtherIndexValue, otherRowCount);
//            double subIndex = (rowCount/dataSize) * (1 - probSum1) + (otherRowCount/dataSize) * (1 - probSum2);
//            indexMap.put(subset,subIndex);
//        }
//        Map<Set<Integer>,Double> sortedIndexMap = indexMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
//        Iterator<Map.Entry<Set<Integer>, Double>> sortedIterator = sortedIndexMap.entrySet().iterator();
//        if (sortedIterator.hasNext()){
//            Map.Entry<Set<Integer>, Double> mapEntry = sortedIterator.next();
//            Set<Integer> attributeValues = mapEntry.getKey();
//            Set<Integer> otherValues = new HashSet<>(attributeInfo.getUniqueAttributeValues());
//            otherValues.removeAll(attributeValues);
//            double indexVal = mapEntry.getValue();
//            splitResult.setAttributeId(attributeInfo.getAttributeId());
//            splitResult.setAttributeValues(attributeValues);
//            splitResult.setOtherAttributeValues(otherValues);
//            splitResult.setValue(indexVal);
//
//            Map<Integer,Integer> filteredIndexValue = new HashMap<>();
//            Map<Integer,Integer> filteredOtherIndexValue = new HashMap<>();
//
//            attributeInfo.getListOfIndexValue().entrySet().stream()
//                    .filter(entry -> attributeValues.contains(entry.getValue())).forEach(entry -> filteredIndexValue.put(entry.getKey(),entry.getValue()));
//            attributeInfo.getListOfIndexValue().entrySet().stream()
//                    .filter(entry -> !attributeValues.contains(entry.getValue())).forEach(entry -> filteredOtherIndexValue.put(entry.getKey(),entry.getValue()));
//
//            splitResult.setRemainingRows(filteredIndexValue.keySet());
//            splitResult.setRemainingOtherRows(filteredOtherIndexValue.keySet());
//            attributeInfos.forEach(attributeInfo1 -> splitResult.getRemainingAttributes().add(attributeInfo1.getAttributeId()));
//            attributeInfos.forEach(attributeInfo1 -> splitResult.getRemainingOtherAttributes().add(attributeInfo1.getAttributeId()));
//        }
//        return splitResult;
//    }
//
//    private double getProbabilitySum(AttributeInfo attributeInfo, Map<Integer, Integer> filteredIndexValue, double rowCount) {
//        double prob = 0.0;
//        for (int clazz: attributeInfo.getUniqueClasses()){
//            final double[] clazzCount = {0};
//            filteredIndexValue.forEach((index, value) -> {
//                if (attributeInfo.getListOfIndexClass().get(index).equals(clazz)){
//                    clazzCount[0]++;
//                }
//            });
//            prob += Math.pow((clazzCount[0] /rowCount),2);
//        }
//        return prob;
//    }
//
//    public void treverse(Node root, int[] ints) {
//        while (!root.isLeaf()){
//            if (root.getLeftBranchCondition().test(ints[root.getAttributeId()])) {
//                treverse(root.getLeftNode(), ints);
//                break;
//            }else if (root.getRightBranchCondition().test(ints[root.getAttributeId()])){
//                treverse(root.getRightNode(),ints);
//                break;
//            }
//        }
//        if (root.isLeaf()) {
//            System.out.println(root.getLeafValue());
//        }
//    }
//}
