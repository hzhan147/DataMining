package com.data.mining.naive.bayes.classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    private static int [][] dataSet = null;
    private static final Set<Integer> classTypes = new HashSet<>();
    private static final Map<Integer,Set<Integer>> attributeFeaturesMap = new HashMap<>();

    public static void main(String[] args) throws IOException,NumberFormatException {
        loadCSVData();
        if (dataSet != null){
            NaiveBayesClassifier classifier = new NaiveBayesClassifier(dataSet,classTypes,attributeFeaturesMap,0.0);
            classifier.classify(dataSet);
        }else {
            System.exit(1);
        }
    }

    private static void loadCSVData() throws IOException,NumberFormatException {
        List<String> dataStrList = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(System.in))){
            String row = null;
            while ((row = csvReader.readLine()) != null){
                dataStrList.add(row);
            }
        }

        if (!dataStrList.isEmpty()){
            int dataSize = dataStrList.size()-1;
            //read data
            dataSet = new int[dataSize][];
            for (int i = 1; i < dataStrList.size(); i++){
                String[] data = dataStrList.get(i).split(",");
                int [] row = new int[data.length-1];
                // index 0 is a animal name...and last index is class_type
                // if the class_type is -1, it's a training set.
                for (int j = 0; j < data.length; j++){
                    if (j != 0){
                        int datum = Integer.parseInt(data[j]);
                        row[j-1] = datum;
                        if ((j) != data.length-1){
                            attributeFeaturesMap.computeIfAbsent(j - 1, k -> new HashSet<>());
                            attributeFeaturesMap.get(j-1).add(datum);
                        }
                    }
                }
                if (row[row.length-1] != -1) classTypes.add(row[row.length-1]);
                dataSet[i-1] = row;
            }
        }
    }
}


//---------------------------------------------------------------------
class ConditionedProbability {
    private int feature_id;
    private int feature_value;
    private int class_type;
    private double probability;

    public ConditionedProbability(int feature_id, int feature_value, int class_type, double probability) {
        this.feature_id = feature_id;
        this.feature_value = feature_value;
        this.class_type = class_type;
        this.probability = probability;
    }

    public int getFeature_id() {
        return feature_id;
    }

    public void setFeature_id(int feature_id) {
        this.feature_id = feature_id;
    }

    public int getFeature_value() {
        return feature_value;
    }

    public void setFeature_value(int feature_value) {
        this.feature_value = feature_value;
    }

    public int getClass_type() {
        return class_type;
    }

    public void setClass_type(int class_type) {
        this.class_type = class_type;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}

class NaiveBayesClassifier {
    private Map<Integer,Double> classProbabilityMap;
    private List<ConditionedProbability> featureProbabilityList = new ArrayList<>();
    /**
     * Train a naive classifier..
     * @param dataSet the whole dataset that may contain test set (class_type = -1)
     * @param classTypes
     * @param attributeFeaturesMap
     * @return a classifier
     */
    public NaiveBayesClassifier(int[][] dataSet, Set<Integer> classTypes, Map<Integer, Set<Integer>> attributeFeaturesMap, double laplacian_smoothing){
        List<int[]> trainingDataSet = Arrays.stream(dataSet).filter(ints -> ints[ints.length - 1] != -1).collect(Collectors.toList());
        classProbabilityMap = getClassProbs(trainingDataSet,classTypes, trainingDataSet.size());
        if (classProbabilityMap != null){
            attributeFeaturesMap.forEach((featureIndex, features) -> {
                featureProbabilityList.addAll(getFeatureConditionedProbability(trainingDataSet, featureIndex, features, classTypes, laplacian_smoothing));
            });
        }
    }

    private List<ConditionedProbability> getFeatureConditionedProbability(List<int[]> trainingDataSet, Integer featureIndex, Set<Integer> features, Set<Integer> classTypes, double laplacian_count) {
        List<ConditionedProbability>  featureList = new ArrayList<>();
        for (int feature: features){
//            System.out.println("computing feature index: " + featureIndex + " feature:" + feature);
            for (int class_type: classTypes){
                double feature_count = (double) trainingDataSet.stream().filter(ints -> ints[featureIndex] == feature && ints[ints.length-1] == class_type).count();
                featureList.add(new ConditionedProbability(featureIndex,feature,class_type,((feature_count+laplacian_count)/((double) trainingDataSet.stream().filter(ints -> ints[ints.length-1] == class_type).count()+(laplacian_count*features.size())))));
            }
        }
        return featureList;
    }

    private Map<Integer, Double> getClassProbs(List<int[]> trainingDataSet, Set<Integer> classTypes, int trainingDataSize) {
        Map<Integer,Double> classProbsMap = new HashMap<>();
        for (int classType: classTypes){
            double num_set = (double) trainingDataSet.stream().filter(ints -> ints[ints.length-1] == classType).count();
            double probability = (num_set / (double)trainingDataSize);
            classProbsMap.put(classType,probability);
        }
        return classProbsMap.isEmpty() ? null : classProbsMap;
    }

    public void classify(int[][] testSet){
        if (classProbabilityMap != null){
            List<int[]> testDataSet = Arrays.stream(testSet).filter(ints -> ints[ints.length - 1] == -1).collect(Collectors.toList());
            for (int[] set: testDataSet){
                Map<Integer,Double> sortedPredictedMap = new LinkedHashMap<>();
                Map<Integer,Double> predictedMap = new LinkedHashMap<>();
                for (int i = 0; i < set.length-1; i++){
                    int feature_value = set[i];
                    for (int clazz_type: classProbabilityMap.keySet()){
                        predictedMap.putIfAbsent(clazz_type, classProbabilityMap.get(clazz_type));
                        double probability = predictedMap.get(clazz_type);
                        int finalI = i;
                        ConditionedProbability conditionedProbability = featureProbabilityList.stream()
                                .filter(conditionedProbability1 -> conditionedProbability1.getFeature_id() == finalI
                                        && conditionedProbability1.getFeature_value() == feature_value
                                        && conditionedProbability1.getClass_type() == clazz_type).findFirst().orElseGet(() -> null);
                        if (conditionedProbability!= null) predictedMap.put(clazz_type,probability*conditionedProbability.getProbability());
                    }
                }
                predictedMap.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> sortedPredictedMap.put(x.getKey(), x.getValue()));
                if (sortedPredictedMap.isEmpty()){
                    System.out.println(-1);
                }else{
                    System.out.println(sortedPredictedMap.keySet().iterator().next());
                }
            }
        }
    }
}
