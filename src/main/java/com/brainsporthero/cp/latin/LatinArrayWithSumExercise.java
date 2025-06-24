package com.brainsporthero.cp.latin;

import java.util.List;

import static com.brainsporthero.cp.latin.LatinArrayWithSum.FACTOR_VISIBLE;
import static com.brainsporthero.cp.latin.LatinArrayWithSum.SIZE;


public class LatinArrayWithSumExercise {

    public int[][] grid = new int[SIZE][SIZE];
    public int[][] gridEx = new int[SIZE][SIZE];
    public int[] sumRows = new int[SIZE];
    public int[] sumColumns = new int[SIZE];
    public int sumDiagonal = 0;

    public LatinArrayWithSumExercise(List<Integer> integerList) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = integerList.get(i * SIZE + j);
                if (FACTOR_VISIBLE < Math.random()) {
                    gridEx[i][j] = grid[i][j];
                } else {
                    gridEx[j][i] = 0;
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            sumRows[i] = 0;
            sumColumns[i] = 0;
        }
        sumDiagonal = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sumRows[i] += grid[i][j];
                sumColumns[j] += grid[i][j];
            }
            sumDiagonal += grid[i][i];
        }

    }

}
