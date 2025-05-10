package com.noface;

import com.noface.heuristic.PatternDatabaseHeuristic;
import javafx.util.Pair;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class CornerPatternGenerator {



    public static void main(String[] args) throws SQLException {
        Rubik rubik = new Rubik();
//        rubik.importCornerState(1813222996135840890l);
//        System.out.println(rubik.extractCornerState());
//        for(int i = 0; i < 6; i++){
//            System.out.println(RubikFace.values()[i].ordinal());
//        }
//        CornerPatternGenerator cornerPatternGenerator = new CornerPatternGenerator();
//        cornerPatternGenerator.generateCornerPatternDatabase();
            PatternDatabaseHeuristic.getInstance();
        int[] depths = PatternDatabaseHeuristic.getInstance().getDepths();

        for (long i = 0; i < depths.length; i++) {
            System.out.println("Depth: " + i + " " + depths[(int) i] + " " );
        }
//        cornerPatternGenerator.testState();
    }
    /**
     * Tạo cơ sở dữ liệu cho trạng thái góc của Rubik
     */
    public void generateCornerPatternDatabase() {
        int cnt = 0;
        System.out.println("Generating cornerpattern database...");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        System.out.println("Thời gian bắt đầu: " + now.format(formatter));

        int[] visited = new int[90000000];
        Arrays.fill(visited, -1);
        Queue<Pair<Integer, Integer>> queue = new ArrayDeque<>();
        Rubik startRubik = new Rubik();
        Integer startState = startRubik.exportCornerState();
        visited[startState] = 0;

        queue.add(new Pair<Integer, Integer>(startState, 0));
        while (!queue.isEmpty()) {
            Integer currentCornerState = queue.peek().getKey();
//            System.out.println(currentCornerState);
            Integer depth = queue.peek().getValue();
            queue.poll();
            for(int i = 0; i < 12; i++){

                Rubik rubik = new Rubik();
                rubik.importCornerState(currentCornerState);
                rubik.getBasicMoves().get(i).run();
                Integer nextKey = rubik.exportCornerState();
                if(visited[nextKey] == -1){
                    visited[nextKey] = depth + 1;
                    cnt++;
                    if(cnt % 100000 == 0){
                        System.out.println("Tổng số lượng: " + cnt);
                    }
//                    System.out.println("Current size is " + cnt);
                    queue.add(new Pair<>(nextKey, depth + 1));

                }
            }

        }
        queue = null;
        now = LocalDateTime.now();
        System.out.println("Tổng số lượng: " + cnt);
        System.out.println("Đã chạy xong, chuẩn bị ghi vào database " + now.format(formatter));
        System.out.println("hehe");
        try {
            PatternDatabase dbUtil = PatternDatabase.getInstance();
            dbUtil.saveCornerPattern(visited);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        now = LocalDateTime.now();
        System.out.println("Đã lưu xong vào database " + now.format(formatter));
    }

    public void testState(){
        Rubik rubik = new Rubik();
        rubik.shuffle(20);
        int cornerState = rubik.exportCornerState();
        Rubik another = new Rubik();
        another.importCornerState(cornerState);
        System.out.println(cornerState);
        System.out.println(another.exportCornerState());
        rubik.compareCorner(another);

    }
}
