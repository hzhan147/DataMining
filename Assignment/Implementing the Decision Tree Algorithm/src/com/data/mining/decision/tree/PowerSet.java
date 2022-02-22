//package com.data.mining.decision.tree;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//class PowerSet {
//    public static Set<Set<Integer>> powerSet(Set<Integer> ints) {
//        // convert set to a list
//        List<Integer> S = new ArrayList<>(ints);
//
//        // N stores total number of subsets
//        long N = (long) Math.pow(2, S.size());
//
//        // Set to store subsets
//        Set<Set<Integer>> result = new HashSet<>();
//
//        // generate each subset one by one
//        for (int i = 0; i < N; i++) {
//            Set<Integer> set = new HashSet<>();
//
//            // check every bit of i
//            for (int j = 0; j < S.size(); j++) {
//                // if j'th bit of i is set, add S.get(j) to current set
//                if ((i & (1 << j)) != 0)
//                    set.add(S.get(j));
//            }
//            result.add(set);
//        }
//
//        result.removeIf(integers -> integers.isEmpty() || integers.size() == ints.size());
//        return result;
//    }
//}
