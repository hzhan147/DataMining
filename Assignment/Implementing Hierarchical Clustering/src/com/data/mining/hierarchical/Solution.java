package com.data.mining.hierarchical;

import java.io.*;
import java.util.*;

public class Solution {
    private static double calcEuclideanDist(Point p1, Point p2) {
    if (p1 != null && p2 != null){
        return Math.sqrt((Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2)));
    }
    else{
        System.exit(1);
    }
    return 0;
}

    private static List<List<Double>> constructMatrixDist(int rows, List<Point> data) {
        List<List<Double>> matrix = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<Double> row = new ArrayList<>();
            for (int c = 0; c < rows; c++) {
                if (c < r){
                    row.add(calcEuclideanDist(data.get(c), data.get(r)));
                }else{
                    row.add(0.0);
                }
            }
            matrix.add(row);
        }
        return matrix;
    }

    private static Target getMinTarget(List<List<Double>> matrix) {
        double minVal = Double.MAX_VALUE;
        int X = -1;
        int Y = -1;
        for (int r = 1; r < matrix.size(); r++) {
            for (int c = 0; c < r; c++) {
                if (matrix.get(r).get(c) < minVal) {
                    minVal = matrix.get(r).get(c);
                    X = r;
                    Y = c;
                }
            }
        }

        if (X != -1) {
            return new Target(X, Y, minVal);
        }
        return null;
    }

    public static void main(String[] args) {
        final String NUMBER_REGEX = "^[-0-9. ]+$";
        List<String> dataStream = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String config = null;
        try {
            config = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (config != null && config.matches(NUMBER_REGEX) && config.contains(" ")) {
            String[] configs = config.split(" ");
            if (configs.length < 3){
                System.exit(1);
            }

            int N = Integer.parseInt(configs[0]);
            int K = Integer.parseInt(configs[1]);
            int M = Integer.parseInt(configs[2]);

            if (K == 0 || M > 2 || M < 0){
                return;
            }
            while (dataStream.size() < N) {
                try {
                    dataStream.add(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Point> data = new ArrayList<>();
            dataStream.stream().map(s -> {
                if (s.matches(NUMBER_REGEX) && s.contains(" ")) {
                    String[] dataSplit = s.split(" ");
                    if (dataSplit.length < 2 || dataSplit[0] == null || dataSplit[1] == null){
                        System.exit(1);
                    }
                    return new Point(Double.parseDouble(dataSplit[0]), Double.parseDouble(dataSplit[1]));
                }else if (s.isEmpty()){
                    System.exit(1);
                }
                return null;
            }).forEach(data::add);

            List<List<Point>> clusters = new ArrayList<>();
//            boolean same = true;
//            for (Point datum : data) {
//                if (datum.getX() != datum.getY()) {
//                    same = false;
//                    break;
//                }
//            }
//            if (same){
//                if (M != 2){
//                    List<Point> row = new ArrayList<>(data);
//                    clusters.add(row);
//                    for (int i = 0; i < clusters.size(); i++) {
//                        for (Point point : data) {
//                            if (clusters.get(i).contains(point)) {
//                                System.out.println(i);
//                            }
//                        }
//                    }
//                }else{
//                    System.exit(1);
//                }
//                System.exit(0);
//            }

            for (Point datum : data) {
                List<Point> row = new ArrayList<>();
                row.add(datum);
                clusters.add(row);
            }
            List<List<Double>> matrix = constructMatrixDist(N, data);
            while (clusters.size() > K) {
                Target minTarget = getMinTarget(matrix);
                if (minTarget != null) {
                    matrix = updateMatrixDist(minTarget, matrix, clusters, M);
                }
            }


            for (Point point : data) {
                for (int i = 0; i < clusters.size(); i++) {
                    if (clusters.get(i).contains(point)) {
                        System.out.println(i);
                        System.out.println("Cluster " + i + ": " + point.getX() + "," + point.getY());
                        break;
                    }
                }
            }
        }
    }

    private static List<List<Double>> updateMatrixDist(Target minTarget, List<List<Double>> matrix, List<List<Point>> clusters, int M) {
        List<List<Double>> newMatrix = new ArrayList<>();
        matrix.forEach(distances -> {
            newMatrix.add(new ArrayList<>(distances));
        });

        int xIndex = Math.min(minTarget.getX(),minTarget.getY());
        int outIndex = Math.max(minTarget.getX(),minTarget.getY());

        //remove row
        for (int c = 0; c < newMatrix.get(xIndex).size(); c++){
            newMatrix.get(xIndex).set(c,0.0);
        }
        newMatrix.remove(outIndex);
        //remove col
        newMatrix.forEach(distances -> distances.set(xIndex,0.0));
        newMatrix.forEach(distances -> distances.remove(outIndex));

        int rowIndex = 0;
        int specialRowIndex = xIndex+1;
        for (int r = 0; r < matrix.size(); r ++){
            if (r != minTarget.getX() && r != minTarget.getY()){
                if (M ==0){
                    if(r < xIndex){
                        newMatrix.get(xIndex).set(rowIndex++, Math.min(matrix.get(minTarget.getX()).get(r),matrix.get(minTarget.getY()).get(r)));
                    }else{
                        double xVal = 0.0;
                        double yVal = 0.0;
                        if (minTarget.getX() > r){
                            xVal = matrix.get(minTarget.getX()).get(r);
                        }else{
                            xVal = matrix.get(r).get(minTarget.getX());
                        }

                        if (minTarget.getY() > r){
                            yVal = matrix.get(minTarget.getY()).get(r);
                        }else{
                            yVal = matrix.get(r).get(minTarget.getY());
                        }
                        newMatrix.get(specialRowIndex++).set(rowIndex, Math.min(xVal,yVal));
                    }
                }else if(M==1){
                    if(r < xIndex){
                        newMatrix.get(xIndex).set(rowIndex++, Math.max(matrix.get(minTarget.getX()).get(r),matrix.get(minTarget.getY()).get(r)));
                    }else{
                        double xVal = 0.0;
                        double yVal = 0.0;
                        if (minTarget.getX() > r){
                            xVal = matrix.get(minTarget.getX()).get(r);
                        }else{
                            xVal = matrix.get(r).get(minTarget.getX());
                        }

                        if (minTarget.getY() > r){
                            yVal = matrix.get(minTarget.getY()).get(r);
                        }else{
                            yVal = matrix.get(r).get(minTarget.getY());
                        }
                        newMatrix.get(specialRowIndex++).set(rowIndex, Math.max(xVal,yVal));
                    }
                }else if(M==2){

                    if(r < xIndex){
                        double numberOfFirstEntries = clusters.get(xIndex).size();
                        double numberOfSecondEntries = clusters.get(minTarget.getX()).size();
                        newMatrix.get(xIndex).set(rowIndex++, (numberOfSecondEntries * matrix.get(minTarget.getX()).get(r)+ numberOfFirstEntries * matrix.get(minTarget.getY()).get(r))/ (numberOfFirstEntries + numberOfSecondEntries));
                    }else{
                        double xVal;
                        double yVal;
                        double numberOfFirstEntries = clusters.get(minTarget.getX()).size();;
                        double numberOfSecondEntries = clusters.get(minTarget.getY()).size();

                        if (minTarget.getX() > r){
                            xVal = matrix.get(minTarget.getX()).get(r);
                        }else{
                            xVal = matrix.get(r).get(minTarget.getX());
                        }

                        if (minTarget.getY() > r){
                            yVal = matrix.get(minTarget.getY()).get(r);
                        }else{
                            yVal = matrix.get(r).get(minTarget.getY());
                        }

                        newMatrix.get(specialRowIndex++).set(rowIndex, (numberOfFirstEntries * xVal+ numberOfSecondEntries * yVal)/ (numberOfFirstEntries + numberOfSecondEntries));
                    }
                }
            }
        }

        //update clusters
        if (matrix.size() - 1 == 1) {
            clusters.get(0).addAll(clusters.get(1));
            clusters.remove(1);
        } else {
            clusters.get(xIndex).addAll(clusters.get(outIndex));
            clusters.remove(outIndex);
        }
        return newMatrix;
    }
}

class Target {
    private int X;
    private int Y;
    private double distance;

    public Target(int x, int y, double distance) {
        X = x;
        Y = y;
        this.distance = distance;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

class Point {
    private double X;
    private double y;

    public Point(double x, double y) {
        X = x;
        this.y = y;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}