package com.noface.rubik.heuristic;

import com.noface.rubik.utils.PatternDatabase;
import com.noface.rubik.rubikImpl.Rubik3;

import java.sql.SQLException;

public class PatternDatabaseHeuristic {
    private static PatternDatabaseHeuristic patternDatabaseHeuristic;
    private  int[] depths;

    private PatternDatabaseHeuristic() throws SQLException {
        depths = PatternDatabase.getInstance().loadCornerPatternChunked();
        System.out.println("pattern database heuristic loaded");

    }

    public static PatternDatabaseHeuristic getInstance() {
        if(patternDatabaseHeuristic == null) {
            try {
                patternDatabaseHeuristic = new PatternDatabaseHeuristic();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return patternDatabaseHeuristic;
    }

    public  int getCornerHeuristicValue(Rubik3 rubik3){
            int cornerDepth = depths[rubik3.exportCornerState()];
            return cornerDepth;
    }

    public int[] getDepths() {
        return depths;
    }
}
