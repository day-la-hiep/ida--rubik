package com.noface; // Đảm bảo cùng package với Rubik.java

import com.noface.heuristic.ManhattanHeuristic;
import com.noface.heuristic.PatternDatabaseHeuristic;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
public class IDASolver {

    private static final String[] MOVES = {
            "U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'"
    };
    private Function<Rubik, Integer> heuristicFunction;

    private Rubik solvedStateRubik;




    public IDASolver() {
        this.solvedStateRubik = new Rubik();
        try {
            heuristicFunction = PatternDatabaseHeuristic.getInstance()::getCornerHeuristicValue;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public IDASolver(Function<Rubik, Integer> heuristicFunction) {
        this.solvedStateRubik = new Rubik();
        this.heuristicFunction = heuristicFunction;
    }


//    public List<String> solve(Rubik initialRubik) {
//        int bound = heuristicFunction.apply(initialRubik);
//        System.out.println(bound);
//        List<String> path = new ArrayList<>();
//        SearchState resultSearchState = null;
//        long cnt = 0;
//        while (true) {
//            System.out.println("Bound: " + bound);
//            Queue<SearchState> queue = new LinkedList<>();
//            int minToAdd = Integer.MAX_VALUE;
//            SearchState initState = new SearchState(initialRubik, path, 0);
//            queue.add(initState);
//
//            while(!queue.isEmpty()) {
//                SearchState currentState = queue.poll();
//                if(currentState.getRubik().isSolved()){
//                    resultSearchState = currentState;
//                    break;
//                }
//                for(String move : MOVES){
//                    Rubik nextRubikState = new Rubik(currentState.getRubik());
//                    nextRubikState.applyMove(move);
//                    if(nextRubikState.equals(currentState)){
//                        throw new RuntimeException("Rubik khoong thay doi");
//                    }
//                    int gCost = currentState.getgCost() + 1;
//                    int fCost = gCost + heuristicFunction.apply(nextRubikState);
//                    if(fCost < bound){
//                        path.add(move);
//                        queue.add(new SearchState(nextRubikState, path, gCost));
//                        cnt++;
//                        if(cnt % 100000 == 0){
//                            System.out.println(cnt);
//                        }
//                        path.remove(path.size() - 1);
//                    }
//                }
//            }
//            if(resultSearchState != null){
//                break;
//            }
//            bound += 2;
//        }
//        System.out.println(cnt);
//        return resultSearchState.getPath();
//    }

    /**
     * Hàm tìm kiếm chính của IDA*.
     * 
     * @param initialRubik Trạng thái Rubik ban đầu cần giải.
     * @return Danh sách các bước giải, hoặc null nếu không tìm thấy trong giới hạn.
     */
    public List<String> solve(Rubik initialRubik) {
        int bound = heuristicFunction.apply(initialRubik);
        List<String> path = new ArrayList<>();


        while (true) {
            System.out.println("IDA*: Trying bound = " + bound);
            SearchResult result = search(initialRubik, 0, bound, path, null);

            if (result.foundSolution()) {
                return result.getPath();
            }
            if (result.getMinExceededCost() == Integer.MAX_VALUE) {
                return null;
            }
            bound = result.getMinExceededCost();

            if (bound > 20) {
                System.out.println("IDA*: Bound exceeded practical limit. Stopping.");
                return null;
            }
        }
    }
    /**
     * Heuristic Khoảng cách Manhattan của Sticker đến Mặt Đúng.
     * Tính tổng "khoảng cách mặt" cho mỗi sticker không phải tâm.
     * Khoảng cách mặt: 0 nếu sticker ở đúng mặt, 1 nếu ở mặt kề, 2 nếu ở mặt đối
     * diện.
     * Chia tổng cho một hằng số (ví dụ: 8 hoặc 12) để đảm bảo tính chấp nhận được.
     */

    /**
     * Hàm tìm kiếm đệ quy (Depth-First Search có giới hạn chi phí).
     * 
     * @param currentRubik Trạng thái Rubik hiện tại.
     * @param gCost        Chi phí thực tế (số bước) từ trạng thái ban đầu đến
     *                     currentRubik.
     * @param bound        Ngưỡng chi phí hiện tại (f_limit).
     * @param currentPath  Danh sách các bước đã thực hiện để đến currentRubik.
     * @param lastMove     Lượt xoay cuối cùng đã thực hiện (để tránh các bước lặp
     *                     vô ích).
     * @return SearchResult chứa thông tin tìm kiếm.
     */
    private SearchResult search(Rubik currentRubik, int gCost, int bound, List<String> currentPath, String lastMove) {
        int hCost = heuristicFunction.apply(currentRubik);
        int fCost = gCost + hCost;
        if (fCost > bound) {
            return new SearchResult(false, null, fCost); // Vượt ngưỡng, trả về fCost
        }

        if (currentRubik.isSolved()) { // Cần thêm isSolved() vào Rubik.java
            return new SearchResult(true, new ArrayList<>(currentPath), fCost); // Tìm thấy lời giải
        }

        int minExceededCost = Integer.MAX_VALUE;

        for (String move : MOVES) {
            // Tối ưu hóa: Tránh các bước lặp lại hoặc vô ích
            if (lastMove != null) {
                // 1. Tránh U rồi U' ngay lập tức
                if (move.charAt(0) == lastMove.charAt(0) &&
                        ((move.length() == 1 && lastMove.length() == 2 && lastMove.charAt(1) == '\'') ||
                                (move.length() == 2 && move.charAt(1) == '\'' && lastMove.length() == 1))) {
                    continue;
                }
                // 2. Tránh U rồi U (nên dùng U2, nhưng ở đây MOVES không có U2, F2...)
                // Hoặc tránh quay cùng 1 mặt 3 lần liên tiếp (U U U -> U')
                if (move.charAt(0) == lastMove.charAt(0) && move.length() == lastMove.length()) {
                    // Nếu currentPath có ít nhất 2 phần tử và 2 phần tử cuối cùng là cùng một mặt
                    if (currentPath.size() >= 2) {
                        String secondLastMove = currentPath.get(currentPath.size() - 2);
                        if (move.charAt(0) == secondLastMove.charAt(0) && move.length() == secondLastMove.length()) {
                            continue; // Tránh U U U hoặc U' U' U'
                        }
                    }
                }
            }

            Rubik nextRubikState = new Rubik(currentRubik); // Cần copy constructor trong Rubik.java
            nextRubikState.applyMove(move); // Cần applyMove(String) trong Rubik.java

            currentPath.add(move);
            SearchResult result = search(nextRubikState, gCost + 1, bound, currentPath, move);
            currentPath.remove(currentPath.size() - 1); // Backtrack: Xóa bước vừa thêm khỏi đường đi

            if (result.foundSolution()) {
                return result; // Truyền lời giải lên
            }
            if (result.getMinExceededCost() < minExceededCost) {
                minExceededCost = result.getMinExceededCost();
            }
        }
        return new SearchResult(false, null, minExceededCost); // Không tìm thấy trong ngưỡng này
    }

    // Lớp nội bộ để lưu trữ kết quả tìm kiếm
    private static class SearchResult {
        private boolean found;
        private List<String> path;
        private int minExceededCost; // f-cost nhỏ nhất của các nút vượt ngưỡng

        public SearchResult(boolean found, List<String> path, int minExceededCost) {
            this.found = found;
            this.path = path;
            this.minExceededCost = minExceededCost;
        }

        public boolean foundSolution() {
            return found;
        }

        public List<String> getPath() {
            return path;
        }

        public int getMinExceededCost() {
            return minExceededCost;
        }
    }
    class SearchState{
        private List<String> path;
        private int gCost;
        private Rubik rubik;

        public SearchState(Rubik rubik, List<String> path, int gCost) {
            this.rubik = new Rubik(rubik);
            this.path = new ArrayList<>(path);
            this.gCost = gCost;
        }

        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }

        public int getgCost() {
            return gCost;
        }

        public void setgCost(int gCost) {
            this.gCost = gCost;
        }

        public Rubik getRubik() {
            return rubik;
        }

        public void setRubik(Rubik rubik) {
            this.rubik = rubik;
        }
        public String toString(){
            return gCost + " " + String.join(" ", path) ;
        }
    }
}