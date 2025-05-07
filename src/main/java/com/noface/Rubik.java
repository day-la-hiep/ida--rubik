package com.noface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//               ----------------
//               | 0  | 1  | 2  |
//               ----------------
//               | 3  | 4  | 5  |
//               ----------------
//               | 6  | 7  | 8  |
//               ----------------
//-------------------------------------------------------------
//| 9  | 10 | 11 | 18 | 19 | 20 | 27 | 28 | 29 | 36 | 37 | 38 |
//-------------------------------------------------------------
//| 12 | 13 | 14 | 21 | 22 | 23 | 30 | 31 | 32 | 39 | 40 | 41 |
//-------------------------------------------------------------
//| 15 | 16 | 17 | 24 | 25 | 26 | 33 | 34 | 35 | 42 | 43 | 44 |
//-------------------------------------------------------------
//               ----------------
//               | 45 | 46 | 47 |
//               ----------------
//               | 48 | 49 | 50 |
//               ----------------
//               | 51 | 52 | 53 |
//               ----------------
public class Rubik {
    List<Character> cube = new ArrayList<>(54);

    public Rubik() {
        for (int i = 0; i < 9; i++) cube.add('U');
        for (int i = 0; i < 9; i++) cube.add('L');
        for (int i = 0; i < 9; i++) cube.add('F');
        for (int i = 0; i < 9; i++) cube.add('R');
        for (int i = 0; i < 9; i++) cube.add('B');
        for (int i = 0; i < 9; i++) cube.add('D');
    }

    private void rotateFaceClockwise(int startIndex) {
        List<Integer> pos = Arrays.asList(0, 1, 2, 5, 8, 7, 6, 3);
        List<Character> temp = new ArrayList<>();
        for (int i : pos) temp.add(cube.get(startIndex + i));
        Collections.rotate(temp, 2);
        for (int i = 0; i < pos.size(); i++) cube.set(startIndex + pos.get(i), temp.get(i));
    }

    private void rotateFaceCounterClockwise(int startIndex) {
        List<Integer> pos = Arrays.asList(0, 1, 2, 5, 8, 7, 6, 3);
        List<Character> temp = new ArrayList<>();
        for (int i : pos) temp.add(cube.get(startIndex + i));
        Collections.rotate(temp, -2);
        for (int i = 0; i < pos.size(); i++) cube.set(startIndex + pos.get(i), temp.get(i));
    }

    // ======= Các thao tác ========

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
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50 ,47}, f = {26, 23, 20};
        rotateEdgeClockwise(u, b, d, f);
    }

    public void moveRPrime() {
        rotateFaceCounterClockwise(27);
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50 ,47}, f = {26, 23, 20};
        rotateEdgeClockwise(u, b, d, f);
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

    // ======== Công cụ hoán đổi cạnh ========
    private void rotateEdgeClockwise(int[] a, int[] b, int[] c, int[] d) {
        char[] temp = new char[a.length];
        for (int i = 0; i < a.length; i++) temp[i] = cube.get(a[i]);
        for (int i = 0; i < a.length; i++) cube.set(a[i], cube.get(d[i]));
        for (int i = 0; i < a.length; i++) cube.set(d[i], cube.get(c[i]));
        for (int i = 0; i < a.length; i++) cube.set(c[i], cube.get(b[i]));
        for (int i = 0; i < a.length; i++) cube.set(b[i], temp[i]);
    }

    private void rotateEdgeCounterClockwise(int[] a, int[] b, int[] c, int[] d) {
        rotateEdgeClockwise(d, c, b, a);
    }

    // ======================
    public void print() {
        for (int i = 0; i < 54; i++) {
            System.out.print(cube.get(i) + " ");
            if ((i + 1) % 9 == 0) System.out.println();
        }
    }
}
