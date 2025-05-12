package com.noface.rubik.rubikImpl;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Rubik {
    protected int size;
    protected char[] state;
    public abstract void moveU();
    public abstract void moveUPrime();
    public abstract void moveL();
    public abstract void moveLPrime();
    public abstract void moveR();
    public abstract void moveRPrime();
    public abstract void moveD();
    public abstract void moveDPrime();
    public abstract void moveB();
    public abstract void moveBPrime();
    public abstract void moveF();
    public abstract void moveFPrime();
    public HashMap<RubikMove, Runnable> moves = new HashMap<>();
    public Rubik(char[] state){
        if(state.length != 6 * size * size){
            throw new IllegalArgumentException("Invalid state length");
        }
        this.state = state;
    }

    public Rubik(int size){
        this.size = size;
        state = new char[size * size * 6];
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < size * size; j++){
                state[i * size * size + j] = RubikFace.values()[i].name().charAt(0);
            }
        }
//        for(int i = 0; i < size; i++){
//            System.out.println(Arrays.toString(state));
//        }
        moves.put(RubikMove.U, this::moveU);
//        moves.put(RubikMove.UPrime, this::moveUPrime);
//        moves.put(RubikMove.L, this::moveL);
//        moves.put(RubikMove.LPrime, this::moveLPrime);
        moves.put(RubikMove.F, this::moveF);
//        moves.put(RubikMove.FPrime, this::moveFPrime);
        moves.put(RubikMove.R, this::moveR);
//        moves.put(RubikMove.RPrime, this::moveRPrime);
//        moves.put(RubikMove.B, this::moveB);
//        moves.put(RubikMove.BPrime, this::moveBPrime);
//        moves.put(RubikMove.D, this::moveD);
//        moves.put(RubikMove.DPrime, this::moveDPrime);

    }
    public void shuffle(int cnt){
        while(cnt-- > 0){
            moves.get(RubikMove.values()[(int)(Math.random() * moves.size())]).run();
        }
    }
    public void applyMove(RubikMove move){
        moves.get(move).run();
    }
    public abstract List<String> getSolution();
    public abstract boolean isSolved();
    public abstract Rubik clone();
    public abstract char[] getState();
    public void reset(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < size * size; j++){
                state[i * size * size + j] = RubikFace.values()[i].name().charAt(0);
            }
        }
    }
    public int getSize(){
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rubik rubik)) return false;
        Rubik other = (Rubik) o;
        if(other.getState().length != other.getState().length){return false;}
        for(int i = 0; i < state.length; i++){
            if(state[i] != other.getState()[i]){return false;}
        }
        return true;
    }



    @Override
    public int hashCode() {
        return Objects.hash(size, Arrays.hashCode(state), moves);
    }
}
