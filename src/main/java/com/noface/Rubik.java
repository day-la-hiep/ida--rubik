package com.noface;


import java.util.*;

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
    List<Character> state = new ArrayList<>(54);

    public Rubik() {
        for (int i = 0; i < 9; i++) state.add('U');
        for (int i = 0; i < 9; i++) state.add('L');
        for (int i = 0; i < 9; i++) state.add('F');
        for (int i = 0; i < 9; i++) state.add('R');
        for (int i = 0; i < 9; i++) state.add('B');
        for (int i = 0; i < 9; i++) state.add('D');
    }

    public Rubik(List<Character> state) {
        this.state = new ArrayList<>(state);

    }

    public Rubik(Rubik other) {
        this.state = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            this.state.add(Character.valueOf(other.state.get(i)));
        }
    }

    public List<Runnable> getBasicMoves() {
        return Arrays.asList(
                this::moveU,
                this::moveUPrime,
                this::moveD,
                this::moveDPrime,
                this::moveL,
                this::moveLPrime,
                this::moveR,
                this::moveRPrime,
                this::moveF,
                this::moveFPrime,
                this::moveB,
                this::moveBPrime
        );
    }

    private void rotateFaceClockwise(int startIndex) {
        List<Integer> pos = Arrays.asList(0, 1, 2, 5, 8, 7, 6, 3);
        List<Character> temp = new ArrayList<>();
        for (int i : pos) temp.add(state.get(startIndex + i));
        Collections.rotate(temp, 2);
        for (int i = 0; i < pos.size(); i++) state.set(startIndex + pos.get(i), temp.get(i));
    }

    private void rotateFaceCounterClockwise(int startIndex) {
        List<Integer> pos = Arrays.asList(0, 1, 2, 5, 8, 7, 6, 3);
        List<Character> temp = new ArrayList<>();
        for (int i : pos) temp.add(state.get(startIndex + i));
        Collections.rotate(temp, -2);
        for (int i = 0; i < pos.size(); i++) state.set(startIndex + pos.get(i), temp.get(i));
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
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50, 47}, f = {26, 23, 20};
        rotateEdgeClockwise(u, b, d, f);
    }

    public void moveRPrime() {
        rotateFaceCounterClockwise(27);
        int[] u = {8, 5, 2}, b = {36, 39, 42}, d = {53, 50, 47}, f = {26, 23, 20};
        rotateEdgeCounterClockwise(u, b, d, f);
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

    // THÊM: Phương thức áp dụng lượt xoay dựa trên tên
    public void applyMove(String moveName) {
        switch (moveName) {
            case "U":
                moveU();
                break;
            case "U'":
                moveUPrime();
                break;
            case "D":
                moveD();
                break;
            case "D'":
                moveDPrime();
                break;
            case "L":
                moveL();
                break;
            case "L'":
                moveLPrime();
                break;
            case "R":
                moveR();
                break;
            case "R'":
                moveRPrime();
                break;
            case "F":
                moveF();
                break;
            case "F'":
                moveFPrime();
                break;
            case "B":
                moveB();
                break;
            case "B'":
                moveBPrime();
                break;
            // Nếu bạn thêm U2, F2... vào MOVES trong IDASolver, cần thêm case ở đây
            // case "U2": moveU(); moveU(); break; // Ví dụ
            default:
                System.err.println("Unknown move: " + moveName);
        }
    }

    // THÊM: Kiểm tra trạng thái đã giải
    public boolean isSolved() {
        // Giả định trạng thái giải là trạng thái được tạo bởi constructor Rubik()
        // Cách đơn giản nhất là so sánh từng sticker với trạng thái giải mẫu
        // (Màu của sticker đầu tiên của mỗi mặt sẽ là màu của cả mặt đó)
        for (int faceStart = 0; faceStart < 54; faceStart += 9) {
            char firstStickerColor = state.get(faceStart + 4); // Màu của ô tâm
            for (int i = 0; i < 9; i++) {
                if (state.get(faceStart + i) != firstStickerColor) {
                    return false;
                }
            }
        }
        return true;
    }

    // ======== Công cụ hoán đổi cạnh ========
    private void rotateEdgeClockwise(int[] a, int[] b, int[] c, int[] d) {
        char[] temp = new char[a.length];
        for (int i = 0; i < a.length; i++) temp[i] = state.get(a[i]);
        for (int i = 0; i < a.length; i++) state.set(a[i], state.get(d[i]));
        for (int i = 0; i < a.length; i++) state.set(d[i], state.get(c[i]));
        for (int i = 0; i < a.length; i++) state.set(c[i], state.get(b[i]));
        for (int i = 0; i < a.length; i++) state.set(b[i], temp[i]);
    }

    private void rotateEdgeCounterClockwise(int[] a, int[] b, int[] c, int[] d) {
        rotateEdgeClockwise(d, c, b, a);
    }

    // THÊM: Override equals và hashCode (quan trọng cho việc so sánh trạng thái nếu
    // cần)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Rubik rubik = (Rubik) o;
        return Objects.equals(state, rubik.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    // ======================
    public void print() {
        for (int i = 0; i < 54; i++) {
            System.out.print(state.get(i) + " ");
            if ((i + 1) % 9 == 0) System.out.println();
        }
    }

    public void importCornerState(int cornerState) {
        int encodedCubeIndex = cornerState / 2187;
        int encodedRotationIndex = cornerState % 2187;

        int[] cubeIndex = Utils.numberToPermutation(encodedCubeIndex, 8);
        int[] rotationIndex = new int[8];
        int sum = 0;
        for (int i = 6; i >= 0; i--) {
            rotationIndex[i] = encodedRotationIndex % 3;
            encodedRotationIndex /= 3;
            sum += rotationIndex[i];
        }
        rotationIndex[7] = (3 - (sum % 3)) % 3;
        for (int i = 0; i < 8; i++) {
            String baseCornerStateString = CornerCube.values()[cubeIndex[i]].name();
            String cornerStateString;
            cornerStateString = rotateLeft(baseCornerStateString, rotationIndex[i]);

            int[] cornerIndex = CornerCube.values()[i].getIndex();
            for (int j = 0; j < 3; j++) {
                state.set(cornerIndex[j], cornerStateString.charAt(j));
            }
        }
    }


    public int exportCornerState() {
        int[] cubeIndex = new int[8];
        int[] rotationIndex = new int[7];
        Arrays.fill(cubeIndex, -1);
        Arrays.fill(rotationIndex, -1);
        for (int i = 0; i < 8; i++) {
            int[] cornerIndex = CornerCube.values()[i].getIndex();
            String cornerStateString = "";
            for (int j = 0; j < 3; j++) {
                cornerStateString += state.get(cornerIndex[j]);
            }
            for (CornerCube cornerCube : CornerCube.values()) {
                String baseCornerStateString = cornerCube.name();
                for (int k = 0; k < 3; k++) {
                    if (baseCornerStateString.equals(rotateRight(cornerStateString, k))) {
                        if (i < 7) {
                            rotationIndex[i] = k;
                        }
                        cubeIndex[i] = CornerCube.valueOf(baseCornerStateString).ordinal();
                        break;
                    }
                }
            }
        }

        int encodedCubeIndex = Utils.permutationToNumber(cubeIndex);
        int encodedRotationIndex = 0;
        for (int i = 0; i < 7; i++) {
            encodedRotationIndex = 3 * encodedRotationIndex + rotationIndex[i];
        }
        int encodeCornerState = encodedCubeIndex * 2187 + encodedRotationIndex;
        return encodeCornerState;
    }

    private static String rotateLeft(String s, int k) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            result.append(s.charAt((i - k + s.length()) % s.length()));
        }
        return result.toString();
    }

    private static String rotateRight(String s, int k) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            result.append(s.charAt((i + k + s.length()) % s.length()));
        }
        return result.toString();
    }

    public void shuffle(int n) {
        for (int i = 0; i < n; i++) {
            int randomIndex = (int) (Math.random() * 12);
            this.getBasicMoves().get(randomIndex).run();
        }
    }


    public List<Character> getState() {
        return state;
    }

    public void compareCorner(Rubik other) {
        for (CornerCube cornerCube : CornerCube.values()) {
            int[] cornerIndex = CornerCube.values()[cornerCube.ordinal()].getIndex();
            String thisCorner = "";
            for (int j = 0; j < 3; j++) {
                thisCorner += state.get(cornerIndex[j]);
            }
            String otherCorner = "";
            for (int j = 0; j < 3; j++) {
                otherCorner += other.state.get(cornerIndex[j]);
            }
            if (!thisCorner.equals(otherCorner)) {
                for (int k = 0; k < 3; k++) {
                    System.out.print(cornerIndex[k] + " ");
                }
                System.out.println();
                System.out.println(thisCorner + " != " + otherCorner);
            }
        }
    }
}
