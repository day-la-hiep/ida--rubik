package com.noface.rubik.enums;

import javafx.scene.paint.Color;

public enum RubikFace {

    U('U', Color.WHITE),
    L('L', Color.ORANGE),
    F('F', Color.GREEN),
    R('R', Color.RED),
    B('B', Color.BLUE),
    D('D', Color.YELLOW);
    RubikFace(char notation, Color color) {
        this.notation = notation;
        this.color = color;
    }
    public char getNotation() {
        return notation;
    }
    public Color getColor() {return color;}
    private Character notation;
    private Color color;
}
