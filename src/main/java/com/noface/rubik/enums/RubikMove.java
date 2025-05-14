package com.noface.rubik.enums;

public enum RubikMove {
    U("U"),
    UPrime("U'"),
    L("L"),
    LPrime("L'"),
    F("F"),
    FPrime("F'"),
    R("R"),
    RPrime("R'"),
    B("B"),
    BPrime("B'"),
    D("D"),
    DPrime("D'"),
    ;

    String notation;

    RubikMove(String notation) {
        this.notation = notation;
    }

    public String getNotation() {
        return notation;
    }
}
