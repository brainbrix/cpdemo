package com.brainsporthero.cp.pythagoras;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class PythagoreanSolverEmpty {
    public static void main(String[] args) {
        // Initialize the model
        Model model = new Model("pythagoras");

        // Define integer variables for a, b, and c
        IntVar a = model.intVar("a", 1, 10); // Set bounds for demonstration
        IntVar b = model.intVar("b", 1, 10);
        IntVar c = model.intVar("c", 1, 100);

        // Add the Pythagorean constraint (a^2 + b^2 = c^2)


        // Solve and print the solution if found
        if (model.getSolver().solve()) {
            System.out.println("Solution found:");
            System.out.println("a: " + a.getValue());
            System.out.println("b: " + b.getValue());
            System.out.println("c: " + c.getValue());
        }
    }
}
