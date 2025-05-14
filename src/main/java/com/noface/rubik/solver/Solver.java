package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik2;

import java.util.List;

public interface Solver {
    SolutionResult solve(Rubik2 rubik);
    void stopSolving();
}
