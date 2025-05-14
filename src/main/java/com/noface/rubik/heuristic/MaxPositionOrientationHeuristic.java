package com.noface.rubik.heuristic;

import com.noface.rubik.rubikImpl.Rubik2;

public class MaxPositionOrientationHeuristic {
    public static int maxOfPositionAndOrientation(Rubik2 cube) {
            return Math.max(
                    MisplacedCornersHeuristic.misplacedCorners(cube),
                    WrongOrientationHeuristic.wrongOrientationOnly(cube));

    }
}