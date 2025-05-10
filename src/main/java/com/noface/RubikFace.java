package com.noface;

public enum RubikFace {

    U('U'),
    L('L'),
    F('F'),
    R('R'),
    B('B'),
    D('D');
    RubikFace(char notation) {
        this.notation = notation;
    }
    public char getNotation() {
        return notation;
    }
    private Character notation;
}
