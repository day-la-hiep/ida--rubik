package com.noface.rubik.solver; // Đảm bảo cùng package với Rubik3.java

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.heuristic.ManhattanHeuristic;
import com.noface.rubik.rubikImpl.Rubik;

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
    private Function<Rubik, Integer> heuristicFunction;

    public Function<Rubik, Integer> getHeuristicFunction() {
        return heuristicFunction;
    }

    public void setHeuristicFunction(Function<Rubik, Integer> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    private IDASolver() {
        heuristicFunction = ManhattanHeuristic::getValue;

    }

    public IDASolver(Function<Rubik, Integer> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }


    public void stopSolving() {
        isStopped = true;
    }

    public List<RubikMove> solve(Rubik initialRubik) {
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
            bound++;
        }
        if(resultSearchState != null){
            return resultSearchState.path;
        }
        return null;
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