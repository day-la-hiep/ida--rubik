package com.noface.rubik.rubikImpl;


import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.utils.Utils;

import java.util.*;

//             -----------
//             |  0 |  1 |
//             -----------
//             |  2 |  3 |
//             -----------
//-------------------------------------------
//|  4 |  5 |   8 |  9 |   12 | 13 |   16 | 17 |
//|  6 |  7 |  10 | 11 |   14 | 15 |   18 | 19 |
//-------------------------------------------
//             -----------
//             | 20 | 21 |
//             -----------
//             | 22 | 23 |
//             -----------
public class Rubik3 extends Rubik {
    public Rubik3() {
        super(3);

    }
    public Rubik3(char[] state){
        super(state);
    }


    public Rubik3 clone(){
        char[] newState = new char[54];
        for(int i = 0; i < 54; i++){
            newState[i] = state[i];
        }
        return new Rubik3(newState);
    }





    public List<Runnable> getBasicMoves() {
        return Arrays.asList(
                this::moveU,
                this::moveUPrime,
                this::moveD,
                this::moveDPrime,
                this::moveL,
                this::moveLPrime,
                this::moveR,
                this::moveRPrime,
                this::moveF,
                this::moveFPrime,
                this::moveB,
                this::moveBPrime
        );
    }




    // Chỉ số theo mặt trên và các chỉ số liên quan
//        38, 37, 36,
//    9 , 0 , 1 , 2 , 29
//    10, 3 , 4 , 5 , 28
//    11, 6 , 7 , 8 , 27
//        18, 19, 20
    public void moveU() {
        rotateFaceClockwise(0);
        int[] l = {9, 10, 11}, f = {18, 19, 20}, r = {27, 28, 29}, b = {36, 37, 38};
        rotateEdgeCounterClockwise(l, f, r, b);
    }

    public void moveUPrime() {
        rotateFaceCounterClockwise(0);
        int[] l = {9, 10, 11}, f = {18, 19, 20}, r = {27, 28, 29}, b = {36, 37, 38};
        rotateEdgeClockwise(l, f, r, b);
    }
/*
    Chỉ số theo mặt dưới  và các chỉ số liên quan
            24, 25, 26
        17, 45, 46, 47, 33
        16, 48, 49, 50, 34
        16, 51, 52, 53, 35
            44, 43, 42,
*/

    public void moveD() {
        rotateFaceClockwise(45);
        int[] l = {15, 16, 17}, f = {24, 25, 26}, r = {33, 34, 35}, b = {42, 43, 44};
        rotateEdgeClockwise(l, f, r, b);
    }

    public void moveDPrime() {
        rotateFaceCounterClockwise(45);
        int[] l = {15, 16, 17}, f = {24, 25, 26}, r = {33, 34, 35}, b = {42, 43, 44};
        rotateEdgeCounterClockwise(l, f, r, b);
    }
/*
    Chỉ số theo mặt trái  và các chỉ số liên quan
        0 , 3 , 6 ,
    38, 9 , 10, 11, 18
    41, 12, 13, 14, 21
    44, 15, 16, 17, 24
        51, 48, 45
*/

    public void moveL() {
        rotateFaceClockwise(9);
        int[] u = {0, 3, 6}, f = {18, 21, 24}, d = {45, 48, 51}, b = {44, 41, 38};
        rotateEdgeClockwise(u, f, d, b);
    }

    public void moveLPrime() {
        rotateFaceCounterClockwise(9);
        int[] u = {0, 3, 6}, f = {18, 21, 24}, d = {45, 48, 51}, b = {44, 41, 38};
        rotateEdgeCounterClockwise(u, f, d, b);
    }

    /*
        Chỉ số theo mặt phải và các chỉ số liên quan
                8 , 5 , 2 ,
            20, 27, 28, 29, 36
            23, 30, 31, 32, 39
            26, 33, 34, 35, 42
                47, 50, 53
    */
    public void moveR() {
        rotateFaceClockwise(27);
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50, 47}, f = {26, 23, 20};
        rotateEdgeClockwise(u, b, d, f);
    }

    public void moveRPrime() {
        rotateFaceCounterClockwise(27);
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50, 47}, f = {26, 23, 20};
        rotateEdgeCounterClockwise(u, b, d, f);
    }

    /*
        Chỉ số theo mặt trước  và các chỉ số liên quan
                6 , 7 , 8
            11, 18, 19, 20, 27
            14, 21, 22, 23, 30
            17, 24, 25, 26, 33
                45, 46, 47
    */
    public void moveF() {
        rotateFaceClockwise(18);
        int[] u = {6, 7, 8}, r = {27, 30, 33}, d = {47, 46, 45}, l = {17, 14, 11};
        rotateEdgeClockwise(u, r, d, l);
    }

    public void moveFPrime() {
        rotateFaceCounterClockwise(18);
        int[] u = {6, 7, 8}, r = {27, 30, 33}, d = {47, 46, 45}, l = {17, 14, 11};
        rotateEdgeCounterClockwise(u, r, d, l);
    }

    /*
        Chỉ số theo mặt sau và các chỉ số liên quan
                2 , 1 , 0
            29, 36, 37, 38, 9
            32, 39, 40, 41, 12
            35, 42, 43, 44, 15
                53, 52, 51

    */
    public void moveB() {
        rotateFaceClockwise(36);
        int[] u = {2, 1, 0}, r = {9, 12, 15}, d = {51, 52, 53}, l = {35, 32, 29};
        rotateEdgeClockwise(u, r, d, l);
    }

    public void moveBPrime() {
        rotateFaceCounterClockwise(36);
        int[] u = {2, 1, 0}, r = {9, 12, 15}, d = {51, 52, 53}, l = {35, 32, 29};
        rotateEdgeCounterClockwise(u, r, d, l);
    }



    public boolean isSolved() {
        // Giả định trạng thái giải là trạng thái được tạo bởi constructor Rubik3()
        // Cách đơn giản nhất là so sánh từng sticker với trạng thái giải mẫu
        // (Màu của sticker đầu tiên của mỗi mặt sẽ là màu của cả mặt đó)
        for (int faceStart = 0; faceStart < 54; faceStart += 9) {
            char firstStickerColor = state[faceStart + 4];
            for (int i = 0; i < 9; i++) {
                if (state[faceStart + i] != firstStickerColor) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getSize() {
        return 3;
    }

    public void importCornerState(int cornerState) {
        int encodedCubeIndex = cornerState / 2187;
        int encodedRotationIndex = cornerState % 2187;

        int[] cubeIndex = Utils.numberToPermutation(encodedCubeIndex, 8);
        int[] rotationIndex = new int[8];
        int sum = 0;
        for (int i = 6; i >= 0; i--) {
            rotationIndex[i] = encodedRotationIndex % 3;
            encodedRotationIndex /= 3;
            sum += rotationIndex[i];
        }
        rotationIndex[7] = (3 - (sum % 3)) % 3;
        for (int i = 0; i < 8; i++) {
            String baseCornerStateString = CornerCube.values()[cubeIndex[i]].name();
            String cornerStateString;
            cornerStateString = rotateLeft(baseCornerStateString, rotationIndex[i]);

            int[] cornerIndex = CornerCube.values()[i].getIndex();
            for (int j = 0; j < 3; j++) {
                state[cornerIndex[j]] = cornerStateString.charAt(j);
            }
        }
    }


    public int exportCornerState() {
        int[] cubeIndex = new int[8];
        int[] rotationIndex = new int[7];
        Arrays.fill(cubeIndex, -1);
        Arrays.fill(rotationIndex, -1);
        for (int i = 0; i < 8; i++) {
            int[] cornerIndex = CornerCube.values()[i].getIndex();
            String cornerStateString = "";
            for (int j = 0; j < 3; j++) {
                cornerStateString += state[cornerIndex[j]];
            }
            for (CornerCube cornerCube : CornerCube.values()) {
                String baseCornerStateString = cornerCube.name();
                for (int k = 0; k < 3; k++) {
                    if (baseCornerStateString.equals(rotateRight(cornerStateString, k))) {
                        if (i < 7) {
                            rotationIndex[i] = k;
                        }
                        cubeIndex[i] = CornerCube.valueOf(baseCornerStateString).ordinal();
                        break;
                    }
                }
            }
        }

        int encodedCubeIndex = Utils.permutationToNumber(cubeIndex);
        int encodedRotationIndex = 0;
        for (int i = 0; i < 7; i++) {
            encodedRotationIndex = 3 * encodedRotationIndex + rotationIndex[i];
        }
        int encodeCornerState = encodedCubeIndex * 2187 + encodedRotationIndex;
        return encodeCornerState;
    }





    @Override
    public List<String> getSolution() {
        return List.of();
    }


    public char[] getState() {
        return state;
    }

    private void rotateEdgeClockwise(int[] a, int[] b, int[] c, int[] d) {
        char[] temp = new char[a.length];
        for (int i = 0; i < a.length; i++) temp[i] = state[a[i]];
        for (int i = 0; i < a.length; i++) state[a[i]] = state[d[i]];
        for (int i = 0; i < a.length; i++) state[d[i]] = state[c[i]];
        for (int i = 0; i < a.length; i++) state[c[i]] = state[b[i]];
        for (int i = 0; i < a.length; i++) state[b[i]] = temp[i];
    }

    private void rotateEdgeCounterClockwise(int[] a, int[] b, int[] c, int[] d) {
        rotateEdgeClockwise(d, c, b, a);
    }
    private static String rotateLeft(String s, int k) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            result.append(s.charAt((i - k + s.length()) % s.length()));
        }
        return result.toString();
    }

    private static String rotateRight(String s, int k) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            result.append(s.charAt((i + k + s.length()) % s.length()));
        }
        return result.toString();
    }
    private void rotateFaceClockwise(int startIndex) {
        char[] pos = new char[]{0, 1, 2, 5, 8, 7, 6, 3};
        char[] temp = new char[pos.length];
        for(int i = 0; i < pos.length; i++) temp[i] = state[startIndex + pos[i]];
        for(int i = 0; i < pos.length; i++){
            state[startIndex + pos[i]] = temp[(i - 2 + pos.length) % pos.length];
        }
    }

    private void rotateFaceCounterClockwise(int startIndex) {
        char[] pos = new char[]{0, 1, 2, 5, 8, 7, 6, 3};
        char[] temp = new char[pos.length];
        for(int i = 0; i < pos.length; i++) temp[i] = state[startIndex + pos[i]];
        for(int i = 0; i < pos.length; i++){
            state[startIndex + pos[i]] = temp[(i + 2 + pos.length) % pos.length];
        }
    }
}

enum CornerCube {
    UBL(new char[]{'U', 'B', 'L'}, new int[]{0, 38, 9}),
    ULF(new char[]{'U', 'L', 'F'}, new int[]{6, 11, 18}),
    UFR(new char[] {'U', 'F', 'R'},new int[]{8, 20, 27}),
    URB(new char[] {'U', 'R', 'B'}, new int[]{2, 29, 36}),

    DRF(new char[] {'D', 'R', 'F'}, new int[]{47, 33, 26}),
    DFL(new char[] {'D', 'F', 'L'}, new int[]{45, 24, 17}),
    DLB(new char[] {'D', 'L', 'B'}, new int[]{51, 15, 44}),
    DBR(new char[] {'D', 'B', 'R'},new int[]{53, 42, 35});


    private final int[] index;
    private final char[] chars;
    CornerCube(char[] chars, int[] index ) {
        this.index = index;
        this.chars = chars;
    }

    public int[] getIndex() {
        return index;
    }
}
