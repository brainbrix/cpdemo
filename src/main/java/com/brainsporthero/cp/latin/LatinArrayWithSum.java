package com.brainsporthero.cp.latin;

import de.vandermeer.asciitable.AsciiTable;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.FixedIntVarImpl;
import puzzles.LoggingEventObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

public class LatinArrayWithSum {

    public static int SIZE = 4;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 16;
    public static float FACTOR_VISIBLE = 0.50f;

    public static void main(String[] args) {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < MAX_VALUE; i++) {
            list.add(1+i);
        }
        boolean found = false;
        while (!found ) {

            Collections.shuffle(list);
            LatinArrayWithSumExercise exercise = new LatinArrayWithSumExercise(list);

            IntVar[][] grid = new IntVar[SIZE][SIZE];

            Model model = new Model("Latin Square");

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (exercise.gridEx[row][col] > 0) {
                        grid[row][col] = model.intVar(exercise.grid[row][col]);
                    } else {
                        grid[row][col] = model.intVar(format("[%s.%s]", row, col), MIN_VALUE, MAX_VALUE);
                    }
                }
            }
            model.allDifferent(getAllCells(grid)).post();
            for (int rowColumnIndex = 0; rowColumnIndex < SIZE; rowColumnIndex++) {
                model.sum(getCellsInRow(grid, rowColumnIndex), "=", exercise.sumRows[rowColumnIndex]).post();
                model.sum(getCellsInColumn(grid, rowColumnIndex), "=", exercise.sumColumns[rowColumnIndex]).post();
            }
            model.sum(getDiagonalsCells(grid), "=", exercise.sumDiagonal).post();

            printGrid(grid, false, exercise.sumRows, exercise.sumColumns, exercise.sumDiagonal);

            model.getSolver().setEventObserver( new LoggingEventObserver() );
            while (model.getSolver().solve()) {

                model.getSolver().showShortStatistics();
                printGrid(grid, true, exercise.sumRows, exercise.sumColumns, exercise.sumDiagonal);
            }
            System.out.println(model.getSolver().getSolutionCount());
            if (model.getSolver().getSolutionCount() == 1) {
                found = true;
            }
        }
    }

    private static IntVar[] getCellsInRow(IntVar[][] grid, int row) {
        return grid[row];
    }

    private static IntVar[] getCellsInColumn(IntVar[][] grid, int column) {
        return Stream.of(grid).map(row -> row[column]).toArray(IntVar[]::new);
    }

    private static IntVar[] getDiagonalsCells(IntVar[][] grid) {
        List<IntVar> allCells = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            allCells.add( grid[row][row] );
        }
        return allCells.toArray(IntVar[]::new);
    }

    private static IntVar[] getAllCells(IntVar[][] grid) {
        List<IntVar> allCells = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            allCells.addAll(List.of(getCellsInRow(grid, row)));
        }
        return allCells.toArray(IntVar[]::new);
    }
    private static void printGrid(IntVar[][] grid, boolean showSolution, int[] rowSums, int[] columSum, int sumOfDiagonals) {

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
            labels.add(""+rowSums[row]);
            at.addRow(labels);
            at.addRule();

        }
        List<String> labels = new ArrayList<>();
        for (int row = 0; row != SIZE; row++) {
            labels.add(""+columSum[row]);
        }
        labels.add(""+sumOfDiagonals);
        at.addRow(labels);
        at.addRule();
        System.out.println(at.render());
    }
}
