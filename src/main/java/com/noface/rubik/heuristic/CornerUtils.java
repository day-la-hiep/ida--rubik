package com.noface.rubik.heuristic;

import com.noface.rubik.rubikImpl.Rubik2;

public class CornerUtils {
    public static final int[][] CORNER_INDICES = {
            { 1, 12, 9 }, // UFR
            { 0, 13, 16 }, // URB
            { 2, 17, 4 }, // UBL
            { 3, 5, 8 }, // ULF
            { 21, 10, 14 }, // DFR
            { 20, 11, 15 }, // DRB
            { 22, 18, 6 }, // DBL
            { 23, 7, 19 } // DLF
    };
    public static final char[][] SOLVED_COLORS = {
            { 'U', 'R', 'F' }, // UFR
            { 'U', 'B', 'R' }, // URB
            { 'U', 'L', 'B' }, // UBL
            { 'U', 'F', 'L' }, // ULF
            { 'D', 'F', 'R' }, // DFR
            { 'D', 'R', 'B' }, // DRB
            { 'D', 'B', 'L' }, // DBL
            { 'D', 'L', 'F' } // DLF
    };

    public static boolean isCornerInCorrectPosition(char[] state, int i) {
        char[] colors = new char[] {
                state[CORNER_INDICES[i][0]],
                state[CORNER_INDICES[i][1]],
                state[CORNER_INDICES[i][2]]
        };
        char[] solved = SOLVED_COLORS[i];
        for (char c : solved) {
            boolean found = false;
            for (char s : colors)
                if (s == c)
                    found = true;
            if (!found)
                return false;
        }
        return true;
    }

    public static boolean isCornerOriented(char[] state, int i) {
        for (int j = 0; j < 3; j++) {
            if (state[CORNER_INDICES[i][j]] != SOLVED_COLORS[i][j])
                return false;
        }
        return true;
    }
}