package com.noface.rubik.rubikImpl;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rubik2{
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
    private int size;
    private char[] state;
    private Map<RubikMove, Runnable> moves;
    public Rubik2(){
        this.size = 2;

        state = new char[size * size * 6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < size * size; j++) {
                state[i * size * size + j] = RubikFace.values()[i].name().charAt(0);
            }
        }
        moves = new HashMap<>();
        moves.put(RubikMove.U, this::moveU);
        moves.put(RubikMove.F, this::moveF);
        moves.put(RubikMove.R, this::moveR);

    }

    public Rubik2(char[]  state){
        this.state = new char[size * size * 6];
        for(int i = 0; i < state.length; i++){
            this.state[i] = state[i];
        }
        moves = new HashMap<>();
        moves.put(RubikMove.U, this::moveU);
        moves.put(RubikMove.F, this::moveF);
        moves.put(RubikMove.R, this::moveR);
    }


    public void moveU() {
        rotateFaceClockwise(0);
        int [] l = {5, 4},b  = {17, 16},r = {13, 12}, f = {9, 8};
        rotateEdgeClockwise(l, b, r, f);
    }

    public void shuffle(int n){
        while(n-- > 0){
            applyMove(RubikMove.values()[(int) (Math.random() * RubikMove.values().length)]);
        }
    }


    public void moveR() {
        rotateFaceClockwise(12);
        int[] f = {11, 9}, u = {3, 1}, b = {16, 18}, d = {23, 21};
        rotateEdgeClockwise(f, u, b, d);
    }




    public void moveF() {
        rotateFaceClockwise(8);
        int[] l = {7, 5}, u = {2, 3}, r = {12, 14}, d = {21, 20};
        rotateEdgeClockwise(l, u, r, d);
    }



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


    public boolean equals(Rubik2 o) {
        if(o.getState().length != o.getState().length){return false;}
        for(int i = 0; i < state.length; i++){
            if(state[i] != o.getState()[i]){return false;}
        }
        return true;
    }
    public void reset(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < size * size; j++){
                state[i * size * size + j] = RubikFace.values()[i].name().charAt(0);
            }
        }
    }

    public String getStateHash() {
            return null;
    }

    public void applyMove(RubikMove move) {
        moves.get(move).run();
    }

}
