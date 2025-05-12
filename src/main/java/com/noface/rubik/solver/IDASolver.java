package com.noface.rubik.solver; // Đảm bảo cùng package với Rubik3.java

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.heuristic.ManhattanHeuristic;
import com.noface.rubik.rubikImpl.Rubik;
import com.noface.rubik.rubikImpl.Rubik3;

import java.util.*;
import java.util.function.Function;

public class IDASolver {
    private boolean isStopped = false;
    private static final String[] MOVES = {
            "U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'"
    };
    private Function<Rubik, Integer> heuristicFunction;

    private Rubik3 solvedStateRubik3;


    public IDASolver() {
        this.solvedStateRubik3 = new Rubik3();
        heuristicFunction = ManhattanHeuristic::getValue;

    }

    public IDASolver(Function<Rubik, Integer> heuristicFunction) {
        this.solvedStateRubik3 = new Rubik3();
        this.heuristicFunction = heuristicFunction;
    }


    public void stopSolving() {
        isStopped = true;
    }

    public List<String> solve(Rubik initialRubik) {
        initialRubik = initialRubik.clone();
        int bound = heuristicFunction.apply(initialRubik);
        System.out.println(bound);
        List<RubikMove> path = new ArrayList<>();
        SearchState resultSearchState = null;

        while (true) {
            System.out.println("Bound: " + bound);
            Stack<SearchState> stack = new Stack<>();
            SearchState initState = new SearchState(initialRubik, path, 0);
            stack.push(initState);
            long cnt = 0;
            while (!stack.isEmpty()) {
                if(isStopped) {
                    isStopped = false;
                    return null;
                }
                SearchState currentSearchState = stack.pop();
                cnt++;
                if (currentSearchState.rubik.isSolved()) {
                    resultSearchState = currentSearchState;
                    break;
                }
                for (RubikMove move : RubikMove.values()) {
                    List<RubikMove> currentPath = currentSearchState.path;
                    if(currentPath.size() > 0){
//                        if (move.name().charAt(0) == lastMove.name().charAt(0)
//                                && move.name().length() == lastMove.name().length()) {
//                            // Nếu currentPath có ít nhất 2 phần tử và 2 phần tử cuối cùng là cùng một mặt
//                            if (currentPath.size() >= 2) {
//                                RubikMove secondLastMove = currentPath.get(currentPath.size() - 2);
//                                if (move.name().charAt(0) == secondLastMove.name().charAt(0)
//                                        && move.name().length() == secondLastMove.name().length()) {
//                                    continue; // Tránh U U U hoặc U' U' U'
//                                }
//                            }
//                        }

                        // Tránh U rồi U'
                        RubikMove lastMove = currentPath.getLast();
                        if(move.name().equals(lastMove.name())
                                && move.name().length() != move.name().length()){
                            continue;
                        }
                        // Tránh lặp lại 3 lần

                        if(currentPath.size() > 1){
                            RubikMove secondLastMove = currentPath.get(currentPath.size() - 2);
                            if(move.equals(lastMove) && secondLastMove.equals(lastMove)){
                                continue;
                            }
                        }
                    }
                    Rubik nextRubikState = currentSearchState.rubik.clone();
                    List<RubikMove> nextPath = new ArrayList<>(currentSearchState.path);
                    nextRubikState.applyMove(move);

                    int gCost = currentSearchState.gCost + 1;
                    int fCost = Math.min(gCost + heuristicFunction.apply(nextRubikState), Integer.MAX_VALUE);
                    if (fCost <= bound) {
                        nextPath.add(move);
                        stack.push(new SearchState(nextRubikState, nextPath, gCost));
                        nextPath.removeLast();
                    }
                }
            }
            System.out.println("Opened node in bound " + bound + " is: " + cnt);

            if (resultSearchState != null) {
                break;
            }
            bound += 1;
        }
        List<String> solutions = new ArrayList<>();
        for (RubikMove move : resultSearchState.path) {
            solutions.add(move.getNotation());
        }
        System.out.println("Done solving " + solutions.size() + " solutions");
        return solutions;
    }

    /**
     * Hàm tìm kiếm chính của IDA*.
     *
     * @param initialRubik3 Trạng thái Rubik3 ban đầu cần giải.
     * @return Danh sách các bước giải, hoặc null nếu không tìm thấy trong giới hạn.
     */
    public List<String> solve2(Rubik3 initialRubik3) {
        int bound = heuristicFunction.apply(initialRubik3);
        List<RubikMove> path = new ArrayList<>();


        while (true) {
            System.out.println("IDA*: Trying bound = " + bound);
            SearchResult result = search(initialRubik3, 0, bound, path, null);

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
     * @param currentRubik3 Trạng thái Rubik3 hiện tại.
     * @param gCost         Chi phí thực tế (số bước) từ trạng thái ban đầu đến
     *                      currentRubik3.
     * @param bound         Ngưỡng chi phí hiện tại (f_limit).
     * @param currentPath   Danh sách các bước đã thực hiện để đến currentRubik3.
     * @param lastMove      Lượt xoay cuối cùng đã thực hiện (để tránh các bước lặp
     *                      vô ích).
     * @return SearchResult chứa thông tin tìm kiếm.
     */
    private SearchResult search(Rubik3 currentRubik3, int gCost, int bound, List<RubikMove> currentPath, RubikMove lastMove) {
        int hCost = heuristicFunction.apply(currentRubik3);
        int fCost = gCost + hCost;
        if (fCost > bound) {
            return new SearchResult(false, null, fCost); // Vượt ngưỡng, trả về fCost
        }

        if (currentRubik3.isSolved()) { // Cần thêm isSolved() vào Rubik3.java
            return new SearchResult(true, new ArrayList<>(currentPath), fCost); // Tìm thấy lời giải
        }

        int minExceededCost = Integer.MAX_VALUE;

        for (RubikMove move : RubikMove.values()) {
            // Tối ưu hóa: Tránh các bước lặp lại hoặc vô ích
            if (lastMove != null) {
                // 1. Tránh U rồi U' ngay lập tức
                if (move.name().charAt(0) == lastMove.name().charAt(0) &&
                        ((move.name().length() == 1 && lastMove.name().length() == 2
                                && lastMove.name().charAt(1) == '\'')
                                || (move.name().length() == 2
                                && move.name().charAt(1) == '\''
                                && lastMove.name().length() == 1))) {
                    continue;
                }
                // 2. Tránh U rồi U (nên dùng U2, nhưng ở đây MOVES không có U2, F2...)
                // Hoặc tránh quay cùng 1 mặt 3 lần liên tiếp (U U U -> U')
                if (move.name().charAt(0) == lastMove.name().charAt(0)
                        && move.name().length() == lastMove.name().length()) {
                    // Nếu currentPath có ít nhất 2 phần tử và 2 phần tử cuối cùng là cùng một mặt
                    if (currentPath.size() >= 2) {
                        RubikMove secondLastMove = currentPath.get(currentPath.size() - 2);
                        if (move.name().charAt(0) == secondLastMove.name().charAt(0)
                                && move.name().length() == secondLastMove.name().length()) {
                            continue; // Tránh U U U hoặc U' U' U'
                        }
                    }
                }
            }

            Rubik3 nextRubik3State = currentRubik3.clone();
            nextRubik3State.applyMove(move); // Cần applyMove(String) trong Rubik3.java

            currentPath.add(move);
            SearchResult result = search(nextRubik3State, gCost + 1, bound, currentPath, move);
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
        private List<RubikMove> path;
        private int minExceededCost; // f-cost nhỏ nhất của các nút vượt ngưỡng

        public SearchResult(boolean found, List<RubikMove> path, int minExceededCost) {
            this.found = found;
            this.path = path;
            this.minExceededCost = minExceededCost;
        }

        public boolean foundSolution() {
            return found;
        }

        public List<String> getPath() {
            final List<String> result = new ArrayList<>();
            for (RubikMove move : path) {
                result.add(move.getNotation());
            }
            return result;
        }

        public int getMinExceededCost() {
            return minExceededCost;
        }
    }

    class SearchState {
        public List<RubikMove> path;
        public int gCost;
        public Rubik rubik;

        public SearchState(Rubik rubik, List<RubikMove> path, int gCost) {
            this.rubik = rubik;
            this.path = new ArrayList<>(path);
            this.gCost = gCost;
        }



    }
}