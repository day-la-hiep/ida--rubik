package com.noface;

public enum CornerCube {
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