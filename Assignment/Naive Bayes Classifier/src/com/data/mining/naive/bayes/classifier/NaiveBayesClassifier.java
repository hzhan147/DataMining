//package com.data.mining.naive.bayes.classifier;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class NaiveBayesClassifier {
//    private Map<Integer,Double> classProbabilityMap;
//    private List<ConditionedProbability> featureProbabilityList = new ArrayList<>();
//    /**
//     * Train a naive classifier..
//     * @param dataSet the whole dataset that may contain test set (class_type = -1)
//     * @param classTypes
//     * @param attributeFeaturesMap
//     * @return a classifier
//     */
//    public NaiveBayesClassifier(int[][] dataSet, Set<Integer> classTypes, Map<Integer, Set<Integer>> attributeFeaturesMap, double laplacian_smoothing){
//        List<int[]> trainingDataSet = Arrays.stream(dataSet).filter(ints -> ints[ints.length - 1] != -1).collect(Collectors.toList());
//        classProbabilityMap = getClassProbs(trainingDataSet,classTypes, trainingDataSet.size());
//        if (classProbabilityMap != null){
//            attributeFeaturesMap.forEach((featureIndex, features) -> {
//                featureProbabilityList.addAll(getFeatureConditionedProbability(trainingDataSet, featureIndex, features, classTypes, laplacian_smoothing));
//            });
//        }
//    }
//
//    private List<ConditionedProbability> getFeatureConditionedProbability(List<int[]> trainingDataSet, Integer featureIndex, Set<Integer> features, Set<Integer> classTypes, double laplacian_count) {
//        List<ConditionedProbability>  featureList = new ArrayList<>();
//        for (int feature: features){
////            System.out.println("computing feature index: " + featureIndex + " feature:" + feature);
//            for (int class_type: classTypes){
//                double feature_count = (double) trainingDataSet.stream().filter(ints -> ints[featureIndex] == feature && ints[ints.length-1] == class_type).count();
//                featureList.add(new ConditionedProbability(featureIndex,feature,class_type,((feature_count+laplacian_count)/((double) trainingDataSet.stream().filter(ints -> ints[ints.length-1] == class_type).count()+(laplacian_count*features.size())))));
//            }
//        }
//        return featureList;
//    }
//
//    private Map<Integer, Double> getClassProbs(List<int[]> trainingDataSet, Set<Integer> classTypes, int trainingDataSize) {
//        Map<Integer,Double> classProbsMap = new HashMap<>();
//        for (int classType: classTypes){
//            double num_set = (double) trainingDataSet.stream().filter(ints -> ints[ints.length-1] == classType).count();
//            double probability = (num_set / (double)trainingDataSize);
//            classProbsMap.put(classType,probability);
//        }
//        return classProbsMap.isEmpty() ? null : classProbsMap;
//    }
//
//    public void classify(int[][] testSet){
//        if (classProbabilityMap != null){
//            List<int[]> testDataSet = Arrays.stream(testSet).filter(ints -> ints[ints.length - 1] == -1).collect(Collectors.toList());
//            for (int[] set: testDataSet){
//                Map<Integer,Double> sortedPredictedMap = new LinkedHashMap<>();
//                Map<Integer,Double> predictedMap = new LinkedHashMap<>();
//                for (int i = 0; i < set.length-1; i++){
//                    int feature_value = set[i];
//                    for (int clazz_type: classProbabilityMap.keySet()){
//                        predictedMap.putIfAbsent(clazz_type, classProbabilityMap.get(clazz_type));
//                        double probability = predictedMap.get(clazz_type);
//                        int finalI = i;
//                        ConditionedProbability conditionedProbability = featureProbabilityList.stream()
//                                .filter(conditionedProbability1 -> conditionedProbability1.getFeature_id() == finalI
//                                        && conditionedProbability1.getFeature_value() == feature_value
//                                        && conditionedProbability1.getClass_type() == clazz_type).findFirst().orElseGet(() -> null);
//                        if (conditionedProbability!= null) predictedMap.put(clazz_type,probability*conditionedProbability.getProbability());
//                    }
//                }
//                predictedMap.entrySet()
//                        .stream()
//                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                        .forEachOrdered(x -> sortedPredictedMap.put(x.getKey(), x.getValue()));
//                if (sortedPredictedMap.isEmpty()){
//                    System.out.println(-1);
//                }else{
//                    System.out.println(sortedPredictedMap.keySet().iterator().next());
//                }
//            }
//        }
//    }
//}
