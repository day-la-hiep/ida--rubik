package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik2;

import java.util.*;

public class BFSSolver implements Solver {
    private static BFSSolver solver;
    private boolean isStopped = false;
    public static BFSSolver getInstance() {
        if(solver == null) {
            solver = new BFSSolver();
        }
        return solver;
    }
    private BFSSolver() {

    }
    public SolutionResult solve(Rubik2 rubik) {
        isStopped = false;
        Rubik2 initalRubik = rubik.clone();
        if(rubik.isSolved()){
            return new SolutionResult();
        }
        int nodeOpened = 0;
        int maximmumNodeHold = 0;
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long startTime = System.nanoTime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Set<Integer> visitedRubiks = new HashSet<>();
        Queue<SearchState> queue = new LinkedList<>();
        SearchState initState = new SearchState(rubik , new ArrayList<>(), 0);
        SearchState resultState = null;
        queue.add(initState);
        while (!queue.isEmpty()) {
            maximmumNodeHold = Math.max(maximmumNodeHold, queue.size());
            if(isStopped == true){
                return null;
            }
            SearchState currentSearchState = queue.poll();
            if(currentSearchState.rubik.isSolved()){
                resultState = currentSearchState;
                break;
            }
            for (RubikMove move : RubikMove.values()) {
                List<RubikMove> currentPath = currentSearchState.path;

                Rubik2 nextRubikState = currentSearchState.rubik.clone();

                nextRubikState.applyMove(move);
                Integer rubikState = extractRubikState(nextRubikState);
                if(!visitedRubiks.contains(rubikState)) {
                    visitedRubiks.add(rubikState);
                    nodeOpened++;
                    List<RubikMove> nextPath = new ArrayList<>(currentSearchState.path);
                    nextPath.add(move);
                    visitedRubiks.add(rubikState);
                    queue.add(new SearchState(nextRubikState ,
                            nextPath,
                            currentSearchState.depth + 1));
                }

            }

        }
        long timeUsed = System.nanoTime() - startTime;
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory() - memoryBefore;
        if(resultState != null) {
            return new SolutionResult(nodeOpened, maximmumNodeHold, resultState.path, memoryUsed, timeUsed);
        }

        return null;
    }

    public void stopSolving() {
        isStopped = true;
    }
    public Integer extractRubikState(Rubik2 rubik) {
        int res = 0;
        for(char c : rubik.getState()){
            res = res * 6 + RubikFace.valueOf(Character.toString(c)).ordinal();
        }
        return res;
    }



    class SearchState {
        public List<RubikMove> path;
        public int depth;
        public Rubik2 rubik;

        public SearchState(Rubik2 rubik, List<RubikMove> path, int depth) {
            this.rubik = rubik;
            this.path = new ArrayList<>(path);
            this.depth = depth;
        }



    }


}
