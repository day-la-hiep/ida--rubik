package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik2;

import java.util.*;

public class DFSSolver implements Solver {
    private boolean isStopped = false;
    private final int MAX_DEPTH = 100; // có thể chỉnh tăng lên
    private static DFSSolver instance;
    public static DFSSolver getInstance() {
        if (instance == null) {
            instance = new DFSSolver();
        }
        return instance;
    }
    @Override
    public SolutionResult solve(Rubik2 rubik) {

        Rubik2 initRubik = rubik.clone();
        Stack<SearchState> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        stack.push(new SearchState(initRubik, new ArrayList<>()));
        visited.add(initRubik.getStateHash());


        while (!stack.isEmpty() && !isStopped) {
            SearchState current = stack.pop();

            if (current.rubik.isSolved()) {
                SolutionResult solutionResult = new SolutionResult();
                solutionResult.setMoves(current.path);
                return solutionResult;
            }

            if (current.path.size() >= MAX_DEPTH) continue;

            for (RubikMove move : RubikMove.values()) {
                Rubik2 nextRubik = current.rubik.clone();
                nextRubik.applyMove(move);

                String hash = nextRubik.getStateHash();
                if (!visited.contains(hash)) {
                    visited.add(hash);

                    List<RubikMove> newPath = new ArrayList<>(current.path);
                    newPath.add(move);
                    stack.push(new SearchState(nextRubik, newPath));
                }
            }
        }

        return null;
    }

    static class SearchState {
        Rubik2 rubik;
        List<RubikMove> path;

        SearchState(Rubik2 rubik, List<RubikMove> path) {
            this.rubik = rubik;
            this.path = path;
        }
    }

    @Override
    public void stopSolving() {
        isStopped = true;
    }
}
