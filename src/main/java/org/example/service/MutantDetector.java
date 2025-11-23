package org.example.service;

import org.springframework.stereotype.Component;

@Component
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;

    public boolean isMutant(String[] dna) {
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;
        char[][] matrix = new char[n][n];

        // Convert to char matrix for faster access
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        int sequenceCount = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Check Horizontal
                if (j <= n - SEQUENCE_LENGTH) {
                    if (checkHorizontal(matrix, i, j)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                // Check Vertical
                if (i <= n - SEQUENCE_LENGTH) {
                    if (checkVertical(matrix, i, j)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                // Check Diagonal Descending (Top-Left to Bottom-Right)
                if (i <= n - SEQUENCE_LENGTH && j <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalDescending(matrix, i, j)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                // Check Diagonal Ascending (Bottom-Left to Top-Right)
                if (i >= SEQUENCE_LENGTH - 1 && j <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalAscending(matrix, i, j)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkHorizontal(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        for (int k = 1; k < SEQUENCE_LENGTH; k++) {
            if (matrix[row][col + k] != base) return false;
        }
        return true;
    }

    private boolean checkVertical(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        for (int k = 1; k < SEQUENCE_LENGTH; k++) {
            if (matrix[row + k][col] != base) return false;
        }
        return true;
    }

    private boolean checkDiagonalDescending(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        for (int k = 1; k < SEQUENCE_LENGTH; k++) {
            if (matrix[row + k][col + k] != base) return false;
        }
        return true;
    }

    private boolean checkDiagonalAscending(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        for (int k = 1; k < SEQUENCE_LENGTH; k++) {
            if (matrix[row - k][col + k] != base) return false;
        }
        return true;
    }
}
