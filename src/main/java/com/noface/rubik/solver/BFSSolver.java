package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik;

import java.util.*;

public class BFSSolver {
    public List<String> solve(Rubik rubik) {
        Rubik initalRubik = rubik.clone();
        Set<Integer> visitedRubiks = new HashSet<>();
        Queue<SearchState> queue = new LinkedList<>();
        SearchState initState = new SearchState(rubik , new ArrayList<>(), 0);
        SearchState resultState = null;
        queue.add(initState);
        while (!queue.isEmpty()) {
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
            res = new ArrayList<>();
            for(RubikMove move : resultState.path) {
                res.add(move.getNotation());
            }
        }else{
            res = null;
        }

        return res;
    }

    public void stopSolving() {

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
