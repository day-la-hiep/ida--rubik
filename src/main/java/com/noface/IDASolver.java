package com.noface; // Đảm bảo cùng package với Rubik.java

import java.util.*;

public class IDASolver {

    private static final String[] MOVES = {
            "U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'"
    };

    private Rubik solvedStateRubik;

    // Enum và Map để tính heuristic (giống ManhattanHeuristicCalculator trước đó)
    private enum Face {
        UP, DOWN, LEFT, RIGHT, FRONT, BACK
    }

    private static final Map<Character, Face> TARGET_FACE_FOR_COLOR = new HashMap<>();
    private static final Face[] FACE_OF_STICKER_INDEX = new Face[54];
    private static final int[] CENTER_STICKER_INDICES = { 4, 13, 22, 31, 40, 49 };

    static {
        TARGET_FACE_FOR_COLOR.put('U', Face.UP); // White
        TARGET_FACE_FOR_COLOR.put('L', Face.LEFT); // Orange
        TARGET_FACE_FOR_COLOR.put('F', Face.FRONT); // Green
        TARGET_FACE_FOR_COLOR.put('R', Face.RIGHT); // Red
        TARGET_FACE_FOR_COLOR.put('B', Face.BACK); // Blue
        TARGET_FACE_FOR_COLOR.put('D', Face.DOWN); // Yellow

        for (int i = 0; i <= 8; i++)
            FACE_OF_STICKER_INDEX[i] = Face.UP;
        for (int i = 9; i <= 17; i++)
            FACE_OF_STICKER_INDEX[i] = Face.LEFT;
        for (int i = 18; i <= 26; i++)
            FACE_OF_STICKER_INDEX[i] = Face.FRONT;
        for (int i = 27; i <= 35; i++)
            FACE_OF_STICKER_INDEX[i] = Face.RIGHT;
        for (int i = 36; i <= 44; i++)
            FACE_OF_STICKER_INDEX[i] = Face.BACK;
        for (int i = 45; i <= 53; i++)
            FACE_OF_STICKER_INDEX[i] = Face.DOWN;
    }

    public IDASolver() {
        this.solvedStateRubik = new Rubik();
    }

    /**
     * Heuristic Khoảng cách Manhattan của Sticker đến Mặt Đúng.
     * Tính tổng "khoảng cách mặt" cho mỗi sticker không phải tâm.
     * Khoảng cách mặt: 0 nếu sticker ở đúng mặt, 1 nếu ở mặt kề, 2 nếu ở mặt đối
     * diện.
     * Chia tổng cho một hằng số (ví dụ: 8 hoặc 12) để đảm bảo tính chấp nhận được.
     */
    private int manhattanDistanceHeuristic(Rubik currentRubik) {
        int totalDistanceSum = 0;
        List<Character> currentState = currentRubik.getCubeState();

        for (int i = 0; i < 54; i++) {
            boolean isCenter = false;
            for (int centerIdx : CENTER_STICKER_INDICES) {
                if (i == centerIdx) {
                    isCenter = true;
                    break;
                }
            }
            if (isCenter)
                continue;

            char actualColorChar = currentState.get(i);
            Face targetFaceForColor = TARGET_FACE_FOR_COLOR.get(actualColorChar);
            Face currentFaceOfIndex = FACE_OF_STICKER_INDEX[i];

            totalDistanceSum += getFaceDistance(currentFaceOfIndex, targetFaceForColor);
        }

        return (int) Math.ceil(totalDistanceSum / 8.0);
    }

    private static int getFaceDistance(Face face1, Face face2) {
        if (face1 == face2)
            return 0;
        if ((face1 == Face.UP && face2 == Face.DOWN) || (face1 == Face.DOWN && face2 == Face.UP) ||
                (face1 == Face.LEFT && face2 == Face.RIGHT) || (face1 == Face.RIGHT && face2 == Face.LEFT) ||
                (face1 == Face.FRONT && face2 == Face.BACK) || (face1 == Face.BACK && face2 == Face.FRONT)) {
            return 2;
        }
        return 1;
    }

    /**
     * Hàm tìm kiếm chính của IDA*.
     * 
     * @param initialRubik Trạng thái Rubik ban đầu cần giải.
     * @return Danh sách các bước giải, hoặc null nếu không tìm thấy trong giới hạn.
     */
    public List<String> solve(Rubik initialRubik) {
        int bound = manhattanDistanceHeuristic(initialRubik);
        List<String> path = new ArrayList<>();

        while (true) {
            System.out.println("IDA*: Trying bound = " + bound);
            SearchResult result = search(initialRubik, 0, bound, path, null);

            if (result.foundSolution()) {
                return result.getPath();
            }
            if (result.getMinExceededCost() == Integer.MAX_VALUE) {
                return null;
            }
            bound = result.getMinExceededCost();

            if (bound > 20) {
                System.out.println("IDA*: Bound exceeded practical limit. Stopping.");
                return null;
            }
        }
    }

    /**
     * Hàm tìm kiếm đệ quy (Depth-First Search có giới hạn chi phí).
     * 
     * @param currentRubik Trạng thái Rubik hiện tại.
     * @param gCost        Chi phí thực tế (số bước) từ trạng thái ban đầu đến
     *                     currentRubik.
     * @param bound        Ngưỡng chi phí hiện tại (f_limit).
     * @param currentPath  Danh sách các bước đã thực hiện để đến currentRubik.
     * @param lastMove     Lượt xoay cuối cùng đã thực hiện (để tránh các bước lặp
     *                     vô ích).
     * @return SearchResult chứa thông tin tìm kiếm.
     */
    private SearchResult search(Rubik currentRubik, int gCost, int bound, List<String> currentPath, String lastMove) {
        int hCost = manhattanDistanceHeuristic(currentRubik);
        int fCost = gCost + hCost;

        if (fCost > bound) {
            return new SearchResult(false, null, fCost); // Vượt ngưỡng, trả về fCost
        }

        if (currentRubik.isSolved()) { // Cần thêm isSolved() vào Rubik.java
            return new SearchResult(true, new ArrayList<>(currentPath), fCost); // Tìm thấy lời giải
        }

        int minExceededCost = Integer.MAX_VALUE;

        for (String move : MOVES) {
            // Tối ưu hóa: Tránh các bước lặp lại hoặc vô ích
            if (lastMove != null) {
                // 1. Tránh U rồi U' ngay lập tức
                if (move.charAt(0) == lastMove.charAt(0) &&
                        ((move.length() == 1 && lastMove.length() == 2 && lastMove.charAt(1) == '\'') ||
                                (move.length() == 2 && move.charAt(1) == '\'' && lastMove.length() == 1))) {
                    continue;
                }
                // 2. Tránh U rồi U (nên dùng U2, nhưng ở đây MOVES không có U2, F2...)
                // Hoặc tránh quay cùng 1 mặt 3 lần liên tiếp (U U U -> U')
                if (move.charAt(0) == lastMove.charAt(0) && move.length() == lastMove.length()) {
                    // Nếu currentPath có ít nhất 2 phần tử và 2 phần tử cuối cùng là cùng một mặt
                    if (currentPath.size() >= 2) {
                        String secondLastMove = currentPath.get(currentPath.size() - 2);
                        if (move.charAt(0) == secondLastMove.charAt(0) && move.length() == secondLastMove.length()) {
                            continue; // Tránh U U U hoặc U' U' U'
                        }
                    }
                }
            }

            Rubik nextRubikState = new Rubik(currentRubik); // Cần copy constructor trong Rubik.java
            nextRubikState.applyMove(move); // Cần applyMove(String) trong Rubik.java

            currentPath.add(move);
            SearchResult result = search(nextRubikState, gCost + 1, bound, currentPath, move);
            currentPath.remove(currentPath.size() - 1); // Backtrack: Xóa bước vừa thêm khỏi đường đi

            if (result.foundSolution()) {
                return result; // Truyền lời giải lên
            }
            if (result.getMinExceededCost() < minExceededCost) {
                minExceededCost = result.getMinExceededCost();
            }
        }
        return new SearchResult(false, null, minExceededCost); // Không tìm thấy trong ngưỡng này
    }

    // Lớp nội bộ để lưu trữ kết quả tìm kiếm
    private static class SearchResult {
        private boolean found;
        private List<String> path;
        private int minExceededCost; // f-cost nhỏ nhất của các nút vượt ngưỡng

        public SearchResult(boolean found, List<String> path, int minExceededCost) {
            this.found = found;
            this.path = path;
            this.minExceededCost = minExceededCost;
        }

        public boolean foundSolution() {
            return found;
        }

        public List<String> getPath() {
            return path;
        }

        public int getMinExceededCost() {
            return minExceededCost;
        }
    }
}