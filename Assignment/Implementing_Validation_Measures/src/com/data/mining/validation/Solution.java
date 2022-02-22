package com.data.mining.validation;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Solution {
    private final static String NUMBER_REGEX = "^[0-9]+$";
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public static void main(String[] args) throws IOException {
        List<Pair<Integer,Integer>> clusterPredictedLabels = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String s;
        while((s = br.readLine()) != null)
        {
            if(s.contains(" ")) {
                String[] split = s.split(" ");
                if (split.length == 2 && split[0].matches(NUMBER_REGEX) && split[1].matches(NUMBER_REGEX)) {
                    clusterPredictedLabels.add(new Pair<>(Integer.parseInt(split[0]),Integer.parseInt(split[1])));
                } else {
                    System.exit(1);
                }
            } else {
                System.exit(1);
            }
        }

        if (clusterPredictedLabels.isEmpty()){
            System.exit(1);
        }

        int[][] matrix = constructNewMatrix(clusterPredictedLabels);
        final double[] mutualInformation = {0};
        final double[] entropyOfCluster = {0};
        final double[] entropyOfPartition = {0};
        final double[][] threeMeasures = {new double[3]};

        executor.execute(() -> {
            mutualInformation[0] = calcMutualInformation(matrix);
        });
        executor.execute(() -> {
            entropyOfCluster[0] = calcEntropy(matrix, 0);
        });
        executor.execute(() -> {
            entropyOfPartition[0] = calcEntropy(matrix, 1);
        });
        executor.execute(() -> {
            threeMeasures[0] = calcTPFNFP(matrix);
        });
        executor.shutdown();
        double NMI = calcNMI(mutualInformation[0], entropyOfCluster[0], entropyOfPartition[0]);
        double JC = calcJC(threeMeasures[0]);
        System.out.println(String.format("%.3f %.3f",NMI,JC));
    }

    private static double calcJC(double[] threeMeasures) {
        double tp = threeMeasures[0];
        double fn = threeMeasures[1];
        double fp = threeMeasures[2];
        if (tp+fn+fp != 0 || tp != 0) {
            return tp / (tp + fn + fp);
        }else{
            return 0;
        }
    }

    private static double[] calcTPFNFP(int[][] matrix) {
        double tp = 0.0;
        double fn = 0.0;
        double fp = 0.0;
        double totalInstances = matrix[matrix.length-1][matrix[matrix.length-1].length-1];
        for (int r = 0; r < matrix.length-1; r++) {
            for (int t = 0; t < matrix[r].length-1; t++) {
                if (matrix[r][t] != 0){
                    tp += Math.pow(matrix[r][t],2);
                }
            }
        }
        tp = (tp-totalInstances)/2.0;

        for(int c = 0; c < matrix[matrix.length-1].length-1; c++){
            if (matrix[matrix.length-1][c] != 0){
                fn += Math.pow(matrix[matrix.length-1][c],2);
            }
        }
        fn = ((fn-totalInstances)/2.0) - tp;

        for(int r = 0; r < matrix.length-1; r++){
            if (matrix[r][matrix[r].length-1] != 0){
                fp += Math.pow(matrix[r][matrix[r].length-1],2);
            }
        }
        fp = ((fp-totalInstances)/2.0) - tp;
        return new double[]{tp,fn,fp};
    }

    private static double calcNMI(double mutualInformation, double entropyOfCluster, double entropyOfPartition) {
        if (entropyOfCluster * entropyOfPartition != 0){
            return mutualInformation / (Math.sqrt(entropyOfCluster * entropyOfPartition));
        }else{
            System.exit(1);
        }
        return 0;
    }

    private static double calcEntropy(int[][] matrix, int mode) {
        double entropy = 0.0;
        double totalInstances = matrix[matrix.length-1][matrix[matrix.length-1].length-1];
        if (totalInstances != 0){
            if (mode == 0){ // cluster entropy
                for (int r = 0; r < matrix.length-1; r++) {
                    double prob = matrix[r][matrix[matrix.length-1].length-1]/totalInstances;
                    if (prob != 0){
                        entropy += prob * Math.log(prob);
                    }
                }
            }else if (mode == 1){ //partition entropy
                for (int c = 0; c < matrix[0].length-1; c++){
                    double prob = matrix[matrix.length-1][c]/totalInstances;
                    if (prob != 0){
                        entropy += prob * Math.log(prob);
                    }
                }
            }
            return entropy * -1;
        }else {
            System.exit(1);
        }
        return 0;
    }

    private static double calcMutualInformation(int[][] matrix){
        double mutual = 0.0;
        double totalInstances = matrix[matrix.length-1][matrix[matrix.length-1].length-1];
        if (totalInstances != 0){
            for (int[] ints : matrix) {
                for (int t = 0; t < ints.length; t++) {
                    if (ints[t] != 0.0) {
                        double Pij = ints[t] / totalInstances;
                        double PciPTj = ((ints[ints.length - 1] / totalInstances) * (matrix[matrix.length - 1][t] / totalInstances));
                        if (PciPTj != 0){
                            mutual += Pij * Math.log(Pij / PciPTj);
                        }else {
                            System.exit(1);
                        }
                    }
                }
            }
        }else{
            System.exit(1);
        }
        return mutual;
    }

    private static int[][] constructNewMatrix(List<Pair<Integer,Integer>> clusterPredictedLabels) {
        List<Integer> distinctPredictedLabels = clusterPredictedLabels.stream().map(Pair::getValue).distinct().collect(Collectors.toList());
        List<Integer> distinctClusterLabels = clusterPredictedLabels.stream().map(Pair::getKey).distinct().collect(Collectors.toList());
        int[][] matrix = new int[distinctClusterLabels.size()+1][distinctPredictedLabels.size()+1];
        for (int r = 0; r < distinctClusterLabels.size(); r++) {
            int row_sum = 0;
            for (int c = 0; c < distinctPredictedLabels.size(); c++) {
                int finalR = r;
                int finalC = c;
                matrix[r][c] = (int) clusterPredictedLabels.parallelStream().filter(integerIntegerPair -> integerIntegerPair.getKey() == finalR && integerIntegerPair.getValue() == finalC).count();
                row_sum += matrix[r][c];
            }
            matrix[r][distinctPredictedLabels.size()] = row_sum;
        }

        for(int c = 0; c <= distinctPredictedLabels.size(); c++){
            int col_sum = 0;
            for(int r = 0; r < distinctClusterLabels.size(); r++){
                col_sum += matrix[r][c];
            }
            matrix[distinctClusterLabels.size()][c] = col_sum;
        }
        return matrix;
    }
}
