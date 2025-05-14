package com.noface.rubik.heuristic;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.rubikImpl.Rubik2;

public class ManhattanHeuristic {
    private static final int MAX_MOVE_2X2 = 20;


    public static int getValue(Rubik2 currentRubik) {

        int totalDistanceSum = 0;
        int size = currentRubik.getSize();
        char[] currentState = currentRubik.getState();

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < size * size; j++){
                totalDistanceSum += getFaceDistance(
                        RubikFace.values()[i],
                        RubikFace.valueOf(String.valueOf(
                                currentState[i * size * size + j]
                        ))
                );
            }
        }
            return (int) Math.ceil(totalDistanceSum / 4.0);

    }


    private static int getFaceDistance(RubikFace rubikFace1, RubikFace rubikFace2) {
        if (rubikFace1 == rubikFace2)
            return 0;
        if ((rubikFace1 == RubikFace.U && rubikFace2 == RubikFace.D)
                || (rubikFace1 == RubikFace.D && rubikFace2 == RubikFace.U)
                || (rubikFace1 == RubikFace.L && rubikFace2 == RubikFace.R)
                || (rubikFace1 == RubikFace.R && rubikFace2 == RubikFace.L)
                || (rubikFace1 == RubikFace.F && rubikFace2 == RubikFace.B)
                || (rubikFace1 == RubikFace.B && rubikFace2 == RubikFace.F)) {
            return 2;
        }
        return 1;
    }

}
