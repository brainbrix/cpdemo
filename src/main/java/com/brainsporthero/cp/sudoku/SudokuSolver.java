package com.brainsporthero.cp.sudoku;

import de.vandermeer.asciitable.AsciiTable;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.FixedIntVarImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

public class SudokuSolver {

    private static final int SIZE = 9;
    private static final int SQUARE_SIZE = 3;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = SIZE;

    static public void main(String... args) {

        // the world's hardest sudoku ;)
        // https://puzzling.stackexchange.com/questions/252/how-do-i-solve-the-worlds-hardest-sudoku
        int[][] predefinedRows = {
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 6, 0, 0, 0, 0, 0},
                {0, 7, 0, 0, 9, 0, 2, 0, 0},

                {0, 5, 0, 0, 0, 7, 0, 0, 0},
                {0, 0, 0, 0, 4, 5, 7, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 3, 0},

                {0, 0, 1, 0, 0, 0, 0, 6, 8},
                {0, 0, 8, 5, 0, 0, 0, 1, 0},
                {0, 9, 0, 0, 0, 0, 4, 0, 0},
        };

        int[][] predefinedRowsForDiagonals = {
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 6, 0, 0, 0, 0, 0},
                {0, 7, 0, 0, 9, 0, 0, 0, 0},

                {0, 5, 0, 0, 0, 7, 0, 0, 0},
                {0, 0, 0, 0, 4, 5, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 3, 0},

                {0, 0, 0, 0, 0, 0, 0, 6, 8},
                {0, 0, 8, 5, 0, 0, 0, 1, 0},
                {0, 9, 0, 0, 0, 0, 4, 0, 0},
        };

        Model model = new Model("Sudoku");
        IntVar[][] grid = buildGrid(model, predefinedRows);
        applyConnectionConstraints(model, grid);

        printGrid(grid, false);

        Solver solver = model.getSolver();
        solver.showShortStatistics();
        if (solver.solve()) {
            printGrid(grid, true);
        }
    }

    private static IntVar[][] buildGrid(Model model, int[][] predefinedRows) {

        IntVar[][] grid = new IntVar[SIZE][SIZE];

        for (int row = 0; row != SIZE; row++) {
            for (int col = 0; col != SIZE; col++) {
                int value = predefinedRows[row][col];
                if (value < MIN_VALUE) {
                    grid[row][col] = model.intVar(format("[%s.%s]", row, col), MIN_VALUE, MAX_VALUE);
                } else {
                    grid[row][col] = model.intVar(value);
                }
            }
        }

        return grid;
    }

    private static void applyConnectionConstraints(Model model, IntVar[][] grid) {
        for (int i = 0; i != SIZE; i++) {
            model.allDifferent(getCellsInRow(grid, i)).post();
            model.allDifferent(getCellsInColumn(grid, i)).post();
            model.allDifferent(getCellsInSquare(grid, i)).post();
        }

    }

    private static IntVar[] getCellsInRow(IntVar[][] grid, int row) {
        return grid[row];
    }

    private static IntVar[] getCellsInColumn(IntVar[][] grid, int column) {
        return Stream.of(grid).map(row -> row[column]).toArray(IntVar[]::new);
    }

    private static IntVar[] getCellsInSquare(IntVar[][] grid, int square) {
        List<IntVar> results = new ArrayList<>();
        int startRow = SQUARE_SIZE * (square / (SIZE / SQUARE_SIZE));
        int startColumn = SQUARE_SIZE * (square % (SIZE / SQUARE_SIZE));

        for (int row = startRow; row != startRow + SQUARE_SIZE; row++) {
            for (int column = startColumn; column != startColumn + SQUARE_SIZE; column++) {
                results.add(grid[row][column]);
            }
        }

        return results.toArray(new IntVar[0]);
    }

    private static IntVar[] getCellsInDiagonals(IntVar[][] grid, int direction) {
        List<IntVar> results = new ArrayList<>();
        for (int i = 0; i< SIZE; i++) {
            if (direction==0) {
                results.add(grid[i][i]);
            } else {
                results.add(grid[SIZE-(i+1)][i]);
            }
        }
        return results.toArray(new IntVar[0]);
    }

    private static void printGrid(IntVar[][] grid, boolean showSolution) {

        AsciiTable at = new AsciiTable();
        at.addRule();

        for (int row = 0; row != SIZE; row++) {
            List<String> labels = new ArrayList<>();
            for (int column = 0; column != SIZE; column++) {
                IntVar variable = grid[row][column];

                boolean isOriginalNumber = variable instanceof FixedIntVarImpl;

                // we show all numbers if we're showing the solution, but we always
                // show the original numbers
                boolean shouldShow = showSolution || isOriginalNumber;
                if (!shouldShow) {
                    labels.add("");
                } else {
                    // this is the number value for the cell, if we're showing the solution,
                    // and this is an original value, we want to wrap it in stars
                    String value = String.valueOf(variable.getValue());
                    if (showSolution && isOriginalNumber) {
                        value = "*" + value + "*";
                    }
                    labels.add(value);
                }
            }
            at.addRow(labels);
            at.addRule();
        }

        System.out.println(at.render());
    }

}