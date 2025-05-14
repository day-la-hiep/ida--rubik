package com.noface.rubik.heuristic;

import com.noface.rubik.rubikImpl.Rubik2;

public class HammingDistanceHeuristic {
    public static int hammingDistance(Rubik2 cube) {
        int count = 0;
        char[] state = cube.getState();
        for (int i = 0; i < 8; i++) {
            boolean correctPos = CornerUtils.isCornerInCorrectPosition(state, i);
            boolean correctOri = CornerUtils.isCornerOriented(state, i);
            if (!correctPos || !correctOri)
                count++;
        }
        return count;
    }
}