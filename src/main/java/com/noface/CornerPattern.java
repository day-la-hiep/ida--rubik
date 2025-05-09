package com.noface;

import javafx.util.Pair;

import java.sql.SQLException;
import java.util.*;

public class CornerPattern {


    public static void main(String[] args) {
        CornerPattern cornerPattern  = new CornerPattern();
        cornerPattern.generateCornerPatternDatabase();
    }
    /**
     * Tạo cơ sở dữ liệu cho trạng thái góc của Rubik
     */
    public void generateCornerPatternDatabase() {
        Map<BitSet, Integer> visited = new HashMap<>();
        Queue<Pair<BitSet, Integer>> queue = new ArrayDeque<>();
        Rubik start = new Rubik();
        BitSet startKey = start.extractCornerState();
        visited.put(startKey, 0);
        queue.add(new Pair<BitSet, Integer>(startKey, 0));
        while (!queue.isEmpty()) {
            BitSet currentCornerState = queue.peek().getKey();
            Integer depth = queue.peek().getValue();
            queue.poll();
            for(int i = 0; i < 12; i++){
                Rubik rubik = new Rubik();
                rubik.importCornerState(currentCornerState);
                BitSet currentKey = rubik.extractCornerState();
                rubik.getActions().get(i).run();
                BitSet nextKey = rubik.extractCornerState();
                if (!visited.keySet().contains(nextKey)) {
                    visited.put(nextKey, depth + 1);
                    queue.add(new Pair<BitSet, Integer>(nextKey, depth + 1));
                    System.out.println("Gốc: " + currentKey);
                    System.out.println("Kích thước: " + visited.size());
                    System.out.println("Trạng thái: " + nextKey + " | Độ sâu: " + (depth + 1));
                }
            }

        }
        try {
            DatabaseUtil dbUtil = new DatabaseUtil();
            dbUtil.saveCornerPattern(visited);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
