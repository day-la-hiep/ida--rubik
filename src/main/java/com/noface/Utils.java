package com.noface;

public class Utils {
    // Mã hóa hoán vị thành số (Lehmer code)
    public static int permutationToNumber(int[] perm) {
        int n = perm.length;
        int[] factorial = new int[n];
        boolean[] used = new boolean[n];

        factorial[0] = 1;
        for (int i = 1; i < n; i++)
            factorial[i] = factorial[i - 1] * i;

        int rank = 0;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < perm[i]; j++) {
                if (!used[j]) count++;
            }
            rank += count * factorial[n - 1 - i];
            used[perm[i]] = true;
        }

        return rank;
    }

    // Giải mã số thành hoán vị
    public static int[] numberToPermutation(int rank, int n) {
        int[] perm = new int[n];
        boolean[] used = new boolean[n];
        int[] factorial = new int[n];

        factorial[0] = 1;
        for (int i = 1; i < n; i++)
            factorial[i] = factorial[i - 1] * i;

        for (int i = 0; i < n; i++) {
            int f = factorial[n - 1 - i];
            int k = rank / f;
            rank %= f;

            // chọn phần tử thứ k chưa được dùng
            int count = -1;
            for (int j = 0; j < n; j++) {
                if (!used[j]) count++;
                if (count == k) {
                    perm[i] = j;
                    used[j] = true;
                    break;
                }
            }
        }

        return perm;
    }

}
