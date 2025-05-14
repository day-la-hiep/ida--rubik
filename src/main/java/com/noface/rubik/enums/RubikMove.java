package com.noface.rubik.enums;

public enum RubikMove {
    // Do áp dụng với Rubik 2x2 nên chỉ lấy các move U, R, F
    // Rubik 2x2 có tính chất đối xứng nên các thao tác còn lại đều có thể thực hiện thông qua
    // 3 thao tác U, R, F trên
    U("U"),
    F("F"),
    R("R"),
    ;
    String notation;
    RubikMove(String notation) {
        this.notation = notation;
    }

    public String getNotation() {
        return notation;
    }
}
