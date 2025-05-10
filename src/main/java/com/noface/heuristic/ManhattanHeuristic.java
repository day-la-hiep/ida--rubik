package com.noface.heuristic;

import com.noface.Rubik;
import com.noface.RubikFace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManhattanHeuristic {
    private static final Map<Character, RubikFace> TARGET_FACE_FOR_COLOR = new HashMap<>();
    private static final RubikFace[] RUBIK_FACE_OF_STICKER_INDICES = new RubikFace[54];
    private static final int[] CENTER_STICKER_INDICES = { 4, 13, 22, 31, 40, 49 };

    static {
        TARGET_FACE_FOR_COLOR.put('U', RubikFace.U); // White
        TARGET_FACE_FOR_COLOR.put('L', RubikFace.L); // Orange
        TARGET_FACE_FOR_COLOR.put('F', RubikFace.F); // Green
        TARGET_FACE_FOR_COLOR.put('R', RubikFace.R); // Red
        TARGET_FACE_FOR_COLOR.put('B', RubikFace.B); // Blue
        TARGET_FACE_FOR_COLOR.put('D', RubikFace.D); // Yellow

        for (int i = 0; i <= 8; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.U;
        for (int i = 9; i <= 17; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.L;
        for (int i = 18; i <= 26; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.F;
        for (int i = 27; i <= 35; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.R;
        for (int i = 36; i <= 44; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.B;
        for (int i = 45; i <= 53; i++)
            RUBIK_FACE_OF_STICKER_INDICES[i] = RubikFace.D;
    }
    public static int getValue(Rubik currentRubik) {
        int totalDistanceSum = 0;
        List<Character> currentState = currentRubik.getState();

        for (int i = 0; i < 54; i++) {
            boolean isCenter = false;
            for (int centerIdx : CENTER_STICKER_INDICES) {
                if (i == centerIdx) {
                    isCenter = true;
                    break;
                }
            }
            if (isCenter)
                continue;

            char actualColorChar = currentState.get(i);
            RubikFace targetRubikFaceForColor = TARGET_FACE_FOR_COLOR.get(actualColorChar);
            RubikFace currentRubikFaceOfIndex = RUBIK_FACE_OF_STICKER_INDICES[i];

            totalDistanceSum += getFaceDistance(currentRubikFaceOfIndex, targetRubikFaceForColor);
        }

        return (int) Math.ceil(totalDistanceSum / 8.0);
    }
    private static int getFaceDistance(RubikFace rubikFace1, RubikFace rubikFace2) {
        if (rubikFace1 == rubikFace2)
            return 0;
        if ((rubikFace1 == RubikFace.U && rubikFace2 == RubikFace.D) || (rubikFace1 == RubikFace.D && rubikFace2 == RubikFace.U) ||
                (rubikFace1 == RubikFace.L && rubikFace2 == RubikFace.R) || (rubikFace1 == RubikFace.R && rubikFace2 == RubikFace.L) ||
                (rubikFace1 == RubikFace.F && rubikFace2 == RubikFace.B) || (rubikFace1 == RubikFace.B && rubikFace2 == RubikFace.F)) {
            return 2;
        }
        return 1;
    }

}
