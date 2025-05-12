package com.noface.rubik.generator;

import com.noface.rubik.utils.PatternDatabase;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.heuristic.PatternDatabaseHeuristic;
import com.noface.rubik.rubikImpl.Rubik3;
import javafx.util.Pair;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class CornerPatternGenerator {



    public static void main(String[] args) throws SQLException {
        Rubik3 rubik3 = new Rubik3();
//        rubik3.importCornerState(1813222996135840890l);
//        System.out.println(rubik3.extractCornerState());
//        for(int i = 0; i < 6; i++){
//            System.out.println(RubikFace.values()[i].ordinal());
//        }
        CornerPatternGenerator cornerPatternGenerator = new CornerPatternGenerator();
//        cornerPatternGenerator.generateCornerPatternDatabase();
//            PatternDatabaseHeuristic.getInstance();

        cornerPatternGenerator.testState();
    }
    /**
     * Tạo cơ sở dữ liệu cho trạng thái góc của Rubik3
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
        Rubik3 startRubik3 = new Rubik3();
        Integer startState = startRubik3.exportCornerState();
        visited[startState] = 0;

        queue.add(new Pair<Integer, Integer>(startState, 0));
        while (!queue.isEmpty()) {
            Integer currentCornerState = queue.peek().getKey();
//            System.out.println(currentCornerState);
            Integer depth = queue.peek().getValue();
            queue.poll();
            for(RubikMove move : RubikMove.values()){

                Rubik3 rubik3 = new Rubik3();
                rubik3.importCornerState(currentCornerState);
                rubik3.applyMove(move);
                Integer nextKey = rubik3.exportCornerState();
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
        Rubik3 rubik3 = new Rubik3();
        rubik3.shuffle(5);
        int cornerState = rubik3.exportCornerState();
        Rubik3 another = new Rubik3();
        another.importCornerState(cornerState);
        System.out.println(cornerState);
        System.out.println(another.exportCornerState());

    }

}
