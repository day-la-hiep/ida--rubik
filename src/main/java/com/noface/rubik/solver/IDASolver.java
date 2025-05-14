package com.noface.rubik.solver; // Đảm bảo cùng package với Rubik3.java

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.heuristic.ManhattanHeuristic;
import com.noface.rubik.rubikImpl.Rubik2;

import java.util.*;
import java.util.function.Function;

public class IDASolver implements Solver {
    private static IDASolver instance;

    public static IDASolver getInstance() {
        if (instance == null) {
            instance = new IDASolver();
        }
        return instance;
    }

    private boolean isStopped = false;
    private Function<Rubik2, Integer> heuristicFunction;

    public Function<Rubik2, Integer> getHeuristicFunction() {
        return heuristicFunction;
    }

    public void setHeuristicFunction(Function<Rubik2, Integer> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    private IDASolver() {
        heuristicFunction = ManhattanHeuristic::getValue;

    }

    public IDASolver(Function<Rubik2, Integer> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    public void stopSolving() {
        isStopped = true;
    }

    public SolutionResult solve(Rubik2 initialRubik) {
        // Khởi tạo các thuộc tính đo lường
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long memoryBeforeRun = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();
        int nodeOpened = 0;
        int maximmumNodeHold = 0;

        // Tạo bản copy rubik để không ảnh hưởng đến bản gốc
        initialRubik = initialRubik.clone();

        // Lấy cận đầu tiên theo hàm heuristic
        int bound = heuristicFunction.apply(initialRubik);

        // Khởi tạo biến path và kết quả để lưu kết quả
        List<RubikMove> path = new ArrayList<>();
        SearchState resultSearchState = null;


        // Bắt đầu vòng lặp
        while (true) {
            // Tạo 1 stack rỗng
            Stack<SearchState> stack = new Stack<>();
            // Khởi tạo trạng thái S ban đầu và thêm vào stack
            SearchState initState = new SearchState(initialRubik, path, 0);
            stack.push(initState);

            while (!stack.isEmpty()) {
                // Cập nhật lại biến để ghi nhận số lượng node tối đa trong stack
                maximmumNodeHold = Math.max(maximmumNodeHold, stack.size());
                // Kiểm tra có yêu cầu dừng thuật toán từ người dùng không
                if (isStopped) {
                    isStopped = false;
                    return null;
                }

                // Lấy trạng thái đầu tiên ở đỉnh stack
                SearchState currentSearchState = stack.pop();
                // Kiểm tra đây có phải trạng thái đích không
                if (currentSearchState.rubik.isSolved()) {
                    resultSearchState = currentSearchState;
                    break;
                }
                // Thực hiện các thao tác xoay của Rubik
                for (RubikMove move : RubikMove.values()) {
                    List<RubikMove> currentPath = currentSearchState.path;
                    // Prunning tránh trường hợp xoay 3 lần liên tiếp thì vô nghĩa
                    if (currentPath.size() > 1) {
                        RubikMove lastMove = currentPath.get(currentPath.size() - 1);
                        RubikMove secondLastMove = currentPath.get(currentPath.size() - 2);
                        if (move.equals(lastMove) && secondLastMove.equals(lastMove)) {
                            continue;
                        }
                    }

                    // Tạo bản sao và thực hiện thao tác xoay
                    Rubik2 nextRubikState = currentSearchState.rubik.clone();
                    nextRubikState.applyMove(move);

                    // Thực hiện tính chi phí f và g
                    int gCost = currentSearchState.gCost + 1;
                    int fCost = Math.min(gCost + heuristicFunction.apply(nextRubikState), Integer.MAX_VALUE);

                    // Nếu chi phí f <= ngưỡng thì thêm trạng thái vào stack
                    if (fCost <= bound) {
                        List<RubikMove> nextPath = new ArrayList<>(currentSearchState.path);
                        nextPath.add(move);
                        nodeOpened++;
                        stack.push(new SearchState(nextRubikState, nextPath, gCost));
                    }
                }
            }

            if (resultSearchState != null) {
                break;
            }
            bound++;
        }
        long duration = System.nanoTime() - startTime;
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory() - memoryBeforeRun;
        if (resultSearchState != null) {
            return new SolutionResult(nodeOpened, maximmumNodeHold, resultSearchState.path, memoryUsed, duration);
        }
        return null;
    }

    class SearchState {
        public List<RubikMove> path;
        public int gCost;
        public Rubik2 rubik;

        public SearchState(Rubik2 rubik, List<RubikMove> path, int gCost) {
            this.rubik = rubik;
            this.path = new ArrayList<>(path);
            this.gCost = gCost;
        }

    }
}