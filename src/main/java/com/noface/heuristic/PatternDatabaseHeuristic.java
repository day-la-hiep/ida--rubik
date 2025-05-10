package com.noface.heuristic;

import com.noface.PatternDatabase;
import com.noface.Rubik;

import java.sql.SQLException;

public class PatternDatabaseHeuristic {
    private static PatternDatabaseHeuristic patternDatabaseHeuristic;
    private  int[] depths;

    private PatternDatabaseHeuristic() throws SQLException {
        depths = PatternDatabase.getInstance().loadCornerPatternChunked();
        System.out.println("patternDatabase heuristic loaded");

    }

    public static PatternDatabaseHeuristic getInstance() throws SQLException {
        if(patternDatabaseHeuristic == null) {
            patternDatabaseHeuristic = new PatternDatabaseHeuristic();
        }
        return patternDatabaseHeuristic;
    }

    public  int getCornerHeuristicValue(Rubik rubik){
            int cornerDepth = depths[rubik.exportCornerState()];
            return cornerDepth;
    }

    public int[] getDepths() {
        return depths;
    }
}
