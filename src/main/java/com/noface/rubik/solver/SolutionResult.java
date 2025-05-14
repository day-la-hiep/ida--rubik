package com.noface.rubik.solver;

import com.noface.rubik.enums.RubikMove;

import java.util.ArrayList;
import java.util.List;

public class SolutionResult {
    private int nodeOpened;
    private int maximmumNodeHold;
    private List<RubikMove> moves;
    private long memoryUsed = 0;
    private long timeUsed = 0;

    public SolutionResult(int nodeOpened, int maximmumNodeHold,  List<RubikMove> moves, long memoryUsed, long timeUsed) {
        this.nodeOpened = nodeOpened;
        this.moves = moves;
        this.memoryUsed = memoryUsed;
        this.timeUsed = timeUsed;
        this.maximmumNodeHold = maximmumNodeHold;
    }

    public SolutionResult() {
        nodeOpened = 0;
        moves = new ArrayList<>();
        memoryUsed = 0;
        timeUsed = 0;
    }

    public int getMaximmumNodeHold() {
        return maximmumNodeHold;
    }

    public void setMaximmumNodeHold(int maximmumNodeHold) {
        this.maximmumNodeHold = maximmumNodeHold;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(long timeUsed) {
        this.timeUsed = timeUsed;
    }

    public int getNodeOpened() {
        return nodeOpened;
    }

    public void setNodeOpened(int nodeOpened) {
        this.nodeOpened = nodeOpened;
    }

    public List<RubikMove> getMoves() {
        return moves;
    }

    public void setMoves(List<RubikMove> moves) {
        this.moves = moves;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
}
