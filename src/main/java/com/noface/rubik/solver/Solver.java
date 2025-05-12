package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik;

import java.util.List;

public interface Solver {
    List<RubikMove> solve(Rubik rubik);
    void stopSolving();
}
