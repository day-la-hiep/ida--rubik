package com.noface.rubik.heuristic;

import com.noface.rubik.rubikImpl.Rubik2;

public class MisplacedCornersHeuristic {
    public static int misplacedCorners(Rubik2 cube) {
        int count = 0;
        char[] state = cube.getState();
        for (int i = 0; i < 8; i++) {
            if (!CornerUtils.isCornerInCorrectPosition(state, i))
                count++;
        }
        return count;
    }
}