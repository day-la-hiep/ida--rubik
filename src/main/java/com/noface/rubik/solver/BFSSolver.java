package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik;

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
    public List<RubikMove> solve(Rubik rubik) {
        isStopped = false;
        Rubik initalRubik = rubik.clone();
        if(rubik.isSolved()){
            return Collections.emptyList();
        }
        Set<Integer> visitedRubiks = new HashSet<>();
        Queue<SearchState> queue = new LinkedList<>();
        SearchState initState = new SearchState(rubik , new ArrayList<>(), 0);
        SearchState resultState = null;
        queue.add(initState);
        while (!queue.isEmpty()) {
            if(isStopped == true){
                return null;
            }
            if(visitedRubiks.size() % 100000 == 0){
                System.out.println(visitedRubiks.size());
            }
            SearchState currentSearchState = queue.poll();
            if(currentSearchState.rubik.isSolved()){
                resultState = currentSearchState;
                break;
            }
            for (RubikMove move : RubikMove.values()) {
                List<RubikMove> currentPath = currentSearchState.path;

                Rubik nextRubikState = currentSearchState.rubik.clone();

                nextRubikState.applyMove(move);
                Integer rubikState = extractRubikState(nextRubikState);
                if(!visitedRubiks.contains(rubikState)) {
                    visitedRubiks.add(rubikState);
                    List<RubikMove> nextPath = new ArrayList<>(currentSearchState.path);
                    nextPath.add(move);
                    visitedRubiks.add(rubikState);
                    queue.add(new SearchState(nextRubikState ,
                            nextPath,
                            currentSearchState.depth + 1));
                }

            }

        }
        List<String> res;
        if(resultState != null) {
            return resultState.path;
        }

        return null;
    }

    public void stopSolving() {
        isStopped = true;
    }
    public Integer extractRubikState(Rubik rubik) {
        int res = 0;
        for(char c : rubik.getState()){
            res = res * 6 + RubikFace.valueOf(Character.toString(c)).ordinal();
        }
        return res;
    }



    class SearchState {
        public List<RubikMove> path;
        public int depth;
        public Rubik rubik;

        public SearchState(Rubik rubik, List<RubikMove> path, int depth) {
            this.rubik = rubik;
            this.path = new ArrayList<>(path);
            this.depth = depth;
        }



    }


}
