package com.noface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RubikCornerEncoder54 {

    // Vị trí các sticker góc trong mảng 54 phần tử
    // Mỗi góc có 3 sticker, thứ tự: [U/D, L/R, F/B]
    private static final int[][] CORNER_POSITIONS = {
            // UFL, UFR, UBR, UBL, DFL, DFR, DBR, DBL
            {8, 9, 20},    // UFL (U-face:8, L-face:9, F-face:20)
            {6, 18, 11},   // UFR
            {0, 17, 26},   // UBR
            {2, 15, 24},   // UBL
            {29, 36, 38},  // DFL
            {27, 44, 33},  // DFR
            {35, 53, 42},  // DBR
            {45, 51, 47}   // DBL
    };

    // Mã màu các mặt (theo thứ tự U, L, F, R, B, D)
    private static final Map<Character, Integer> COLOR_INDEX = new HashMap<>();
    static {
        COLOR_INDEX.put('U', 0);
        COLOR_INDEX.put('L', 1);
        COLOR_INDEX.put('F', 2);
        COLOR_INDEX.put('R', 3);
        COLOR_INDEX.put('B', 4);
        COLOR_INDEX.put('D', 5);
    }

    // Thứ tự góc chuẩn (UFL, UFR, UBR, UBL, DFL, DFR, DBR, DBL)
    private static final char[][] STANDARD_CORNERS = {
            {'U', 'L', 'F'}, {'U', 'R', 'F'}, {'U', 'R', 'B'}, {'U', 'L', 'B'},
            {'D', 'L', 'F'}, {'D', 'R', 'F'}, {'D', 'R', 'B'}, {'D', 'L', 'B'}
    };

    public static int encodeFrom54Colors(char[] rubik54) {
        // Bước 1: Trích xuất thông tin góc từ mảng 54 màu
        int[] cornerPermutation = new int[8];
        int[] cornerOrientation = new int[8];

        for (int corner = 0; corner < 8; corner++) {
            int[] positions = CORNER_POSITIONS[corner];
            char[] colors = {
                    rubik54[positions[0]],
                    rubik54[positions[1]],
                    rubik54[positions[2]]
            };

            // Tìm góc nào trong STANDARD_CORNERS khớp với màu hiện tại
            for (int stdCorner = 0; stdCorner < 8; stdCorner++) {
                if (matchesCorner(colors, STANDARD_CORNERS[stdCorner])) {
                    cornerPermutation[corner] = stdCorner;
                    cornerOrientation[corner] = getOrientation(colors, STANDARD_CORNERS[stdCorner]);
                    break;
                }
            }
        }

        // Bước 2: Mã hóa thông tin góc
        return encodeCornerState(cornerPermutation, cornerOrientation);
    }

    private static boolean matchesCorner(char[] colors, char[] standard) {
        // Kiểm tra 2 bộ màu có giống nhau không (không quan tâm thứ tự)
        int count = 0;
        for (char c1 : colors) {
            for (char c2 : standard) {
                if (c1 == c2) count++;
            }
        }
        return count == 3;
    }

    private static int getOrientation(char[] colors, char[] standard) {
        // Xác định hướng góc (0, 1, 2)
        for (int i = 0; i < 3; i++) {
            if (COLOR_INDEX.get(colors[0]) == COLOR_INDEX.get(standard[i])) {
                // Xác định vị trí của màu mặt U/D
                return i;
            }
        }
        return 0;
    }

    private static int encodeCornerState(int[] cornerPermutation, int[] cornerOrientation) {
        int permutationIndex = calculatePermutationIndex(cornerPermutation);
        int orientationIndex = calculateOrientationIndex(cornerOrientation);
        return permutationIndex * 2187 + orientationIndex;
    }

    private static int calculatePermutationIndex(int[] permutation) {
        // Sử dụng Lehmer code
        int index = 0;
        for (int i = 0; i < permutation.length - 1; i++) {
            int count = 0;
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[j] < permutation[i]) {
                    count++;
                }
            }
            index += count * factorial(7 - i);
        }
        return index;
    }

    private static int calculateOrientationIndex(int[] orientation) {
        // Chỉ sử dụng 7 góc đầu
        int index = 0;
        for (int i = 0; i < 7; i++) {
            index = index * 3 + orientation[i];
        }
        return index;
    }

    private static int factorial(int n) {
        if (n <= 1) return 1;
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        // Ví dụ trạng thái giải được (identity)
        char[] solved = new char[54];
        // Điền màu các mặt theo thứ tự U, L, F, R, B, D
        Arrays.fill(solved, 0, 9, 'U');
        Arrays.fill(solved, 9, 18, 'L');
        Arrays.fill(solved, 18, 27, 'F');
        Arrays.fill(solved, 27, 36, 'R');
        Arrays.fill(solved, 36, 45, 'B');
        Arrays.fill(solved, 45, 54, 'D');

        System.out.println("Solved state: " + encodeFrom54Colors(solved));

        // Ví dụ trạng thái xáo trộn (xoay mặt F)
        char[] mixed = solved.clone();
        // Thực hiện xoay mặt F (ảnh hưởng đến 4 góc)
        mixed[6] = 'U'; mixed[18] = 'R'; mixed[11] = 'F';
        mixed[8] = 'L'; mixed[9] = 'F'; mixed[20] = 'U';
        mixed[27] = 'D'; mixed[44] = 'F'; mixed[33] = 'R';
        mixed[29] = 'F'; mixed[36] = 'D'; mixed[38] = 'L';

        System.out.println("Mixed state: " + encodeFrom54Colors(mixed));
    }
}