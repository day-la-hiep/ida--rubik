package com.noface.rubik.rubikImpl;

import com.noface.rubik.enums.RubikFace;

import java.util.List;

public class Rubik2 extends Rubik {
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

    public Rubik2(){
        super(2);

    }

    public Rubik2(char[]  state){
        super(state);

    }

    @Override
    public void moveU() {
        rotateFaceClockwise(0);
        int [] l = {5, 4},b  = {17, 16},r = {13, 12}, f = {9, 8};
        rotateEdgeClockwise(l, b, r, f);
    }

    @Override
    public void moveUPrime() {
        rotateFaceCounterClockwise(0);
        int [] l = {5, 4},b  = {17, 16},r = {13, 12}, f = {9, 8};
        rotateEdgeCounterClockwise(l, b, r, f);
    }

    @Override
    public void moveL() {
        rotateFaceClockwise(4);
        int [] b = {19, 17}, u = {0, 2}, f = {8, 10}, d = {20, 22};
        rotateEdgeClockwise(b, u, f, d);
    }

    @Override
    public void moveLPrime() {
        rotateFaceCounterClockwise(4);
        int [] b = {19, 17}, u = {0, 2}, f = {8, 10}, d = {20, 22};
        rotateEdgeCounterClockwise(b, u, f, d);
    }

    @Override
    public void moveR() {
        rotateFaceClockwise(12);
        int[] f = {11, 9}, u = {3, 1}, b = {16, 18}, d = {23, 21};
        rotateEdgeClockwise(f, u, b, d);
    }

    @Override
    public void moveRPrime() {
        rotateFaceCounterClockwise(12);
        int[] f = {11, 9}, u = {3, 1}, b = {16, 18}, d = {23, 21};
        rotateEdgeCounterClockwise(f, u, b, d);
    }

    @Override
    public void moveD() {
        rotateFaceClockwise(20);
        int[] l = {6, 7}, f = {10, 11}, r = {14, 15}, b = {18, 19};
        rotateEdgeClockwise(l, f, r, b);
    }

    @Override
    public void moveDPrime() {
        rotateFaceCounterClockwise(20);
        int[] l = {6, 7}, f = {10, 11}, r = {14, 15}, b = {18, 19};
        rotateEdgeCounterClockwise(l, f, r, b);
    }

    @Override
    public void moveB() {
        rotateFaceClockwise(16);
        int[] r = {15, 13}, u = {1, 0}, l = {4, 6}, d = {22, 23};
        rotateEdgeClockwise(r, u, l, d);
    }

    @Override
    public void moveBPrime() {
        rotateFaceCounterClockwise(16);
        int[] r = {15, 13}, u = {1, 0}, l = {4, 6}, d = {22, 23};
        rotateEdgeCounterClockwise(r, u, l, d);
    }

    @Override
    public void moveF() {
        rotateFaceClockwise(8);
        int[] l = {7, 5}, u = {2, 3}, r = {12, 14}, d = {21, 20};
        rotateEdgeClockwise(l, u, r, d);
    }

    @Override
    public void moveFPrime() {
        rotateFaceCounterClockwise(8);
        int[] l = {7, 5}, u = {2, 3}, r = {12, 14}, d = {21, 20};
        rotateEdgeCounterClockwise(l, u, r, d);
    }

    @Override
    public List<String> getSolution() {
        return List.of();
    }

    @Override
    public boolean isSolved() {
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 4; j++){
                if(state[i * 4 + j] != RubikFace.values()[i].getNotation()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public Rubik2 clone() {
        Rubik2 rubik = new Rubik2();
        for(int i = 0; i < this.state.length; i++){
            rubik.getState()[i] = this.state[i];
        }
        return rubik;
    }

    @Override
    public char[] getState() {
        return state;
    }



    @Override
    public void shuffle(int n) {
        super.shuffle(n);
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


    private void rotateFaceClockwise(int startIndex) {
        char[] pos = new char[]{0, 1, 3, 2};
        char[] temp = new char[pos.length];
        for(int i = 0; i < pos.length; i++) temp[i] = state[startIndex + pos[(i - 1 + pos.length) % pos.length]];
        for(int i = 0; i < pos.length; i++){
            state[startIndex + pos[i]] = temp[i];
        }
    }

    private void rotateFaceCounterClockwise(int startIndex) {
        char[] pos = new char[]{0, 1, 3, 2};
        char[] temp = new char[pos.length];
        for(int i = 0; i < pos.length; i++) temp[i] = state[startIndex + pos[(i + 1 + pos.length) % pos.length]];
        for(int i = 0; i < pos.length; i++){
            state[startIndex + pos[i]] = temp[i];
        }
    }


    public boolean equals(Rubik o) {
        if(o.getState().length != o.getState().length){return false;}
        for(int i = 0; i < state.length; i++){
            if(state[i] != o.getState()[i]){return false;}
        }
        return true;
    }
}
