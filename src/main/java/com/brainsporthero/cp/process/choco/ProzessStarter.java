package com.brainsporthero.cp.process.choco;

import com.brainsporthero.cp.process.Const;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

import static com.brainsporthero.cp.process.Const.*;
import static com.brainsporthero.cp.process.Factory.getProcessLabel;


public class ProzessStarter {

    // Schritt-Dauer und Kapazität (Beispieldaten)
    public static int[][] dauerWerte = {
            {2, 1, 2, 1}, // Prozess 0
            {1, 3, 2, 2}, // Prozess 1
            {2, 1, 2, 2},  // Prozess 2
            {1, 3, 2, 1}, // Prozess 1
            {2, 1, 2, 2}  // Prozess 2
    };

    public static  int[][] kapazitaetWerte = {
            {2, 1, 2, 3},
            {1, 2, 1, 1},
            {3, 1, 1, 2},
            {1, 2, 1, 2},
            {2, 1, 1, 2}
    };

    public static void main(String[] args) {
        ProzessStarter ps = new ProzessStarter();
        ps.start();
    }

    public void start() {

        int anzahlProzesse = Const.NUMBER_OF_PROCESSES;
        int schritteProProzess = Const.NUMBER_OF_TASKS_PER_PROCESS;
        int maxZeitraum = MAX_TIME;
        int maxKapazitaet = MAX_CAPACITY;

        Model model = new Model("Prozessschritte Optimierung");

        IntVar[][] start = new IntVar[anzahlProzesse][schritteProProzess];
        IntVar[][] dauer = new IntVar[anzahlProzesse][schritteProProzess];
        IntVar[][] kapazitaet = new IntVar[anzahlProzesse][schritteProProzess];
        Task[][] tasks = new Task[anzahlProzesse][schritteProProzess];
        IntVar[] endzeiten = new IntVar[anzahlProzesse];

        // Aufgabenvariablen anlegen
        for (int p = 0; p < anzahlProzesse; p++) {
            for (int s = 0; s < schritteProProzess; s++) {
                dauer[p][s] = model.intVar(dauerWerte[p][s]);
                kapazitaet[p][s] = model.intVar(kapazitaetWerte[p][s]);
                start[p][s] = model.intVar(0, maxZeitraum);
                tasks[p][s] = new Task(start[p][s], dauer[p][s], start[p][s].add(dauer[p][s]).intVar());
            }
        }

        // Reihenfolge der Schritte innerhalb eines Prozesses
        for (int p = 0; p < anzahlProzesse; p++) {
            for (int s = 0; s < schritteProProzess - 1; s++) {
                model.arithm(start[p][s + 1], ">=", start[p][s].add(dauer[p][s]).intVar()).post();
            }
        }

        // Cumulative-Constraint (Kapazitätsgrenze)
        Task[] allTasks = new Task[anzahlProzesse * schritteProProzess];
        IntVar[] allKapazitaeten = new IntVar[anzahlProzesse * schritteProProzess];
        int index = 0;
        for (int p = 0; p < anzahlProzesse; p++) {
            for (int s = 0; s < schritteProProzess; s++) {
                allTasks[index] = tasks[p][s];
                allKapazitaeten[index] = kapazitaet[p][s];
                index++;
            }
        }
        model.cumulative(allTasks, allKapazitaeten, model.intVar(maxKapazitaet)).post();

        // Endzeit pro Prozess berechnen
        for (int p = 0; p < anzahlProzesse; p++) {
            IntVar letzteEnde = start[p][schritteProProzess - 1].add(dauer[p][schritteProProzess - 1]).intVar();
            endzeiten[p] = letzteEnde;
        }

        // Ziel: Gesamtprozess soll möglichst früh enden
        IntVar makespan = model.intVar("makespan", 0, maxZeitraum);
        model.max(makespan, endzeiten).post();
        model.setObjective(Model.MINIMIZE, makespan);

        // Lösung
        Solver solver = model.getSolver();
        solver.limitTime("120s");

        int numberOfFoundSolutions = 0;

        // Einfache Text-Timeline
        String[][] field = new String[MAX_CAPACITY][MAX_TIME];

        while (solver.solve()) {
            numberOfFoundSolutions++;
            initEmptyField(field);
            System.out.println("Optimale Lösung gefunden (minimales Ende aller Prozesse):");
            System.out.println("Gesamte Endzeit: " + makespan.getValue() + "\n");

            if (LOG_PROCESS_DETAILS) {
                for (int p = 0; p < anzahlProzesse; p++) {
                    System.out.println("Prozess " + (p + 1) + ":");
                    for (int s = 0; s < schritteProProzess; s++) {
                        System.out.printf("  Schritt %d - Start: %d, Dauer: %d, Kapazität: %d\n",
                                s,
                                start[p][s].getValue(),
                                dauer[p][s].getValue(),
                                kapazitaet[p][s].getValue());
                    }
                    System.out.println("  -> Endzeit: " + endzeiten[p].getValue() + "\n");
                }
            }


            for (int t = 0; t <= makespan.getValue(); t++) {
                int capProTick = 0;
                for (int p = 0; p < anzahlProzesse; p++) {
                    StringBuilder line = new StringBuilder("P" + p + ": ");

                    boolean aktiv = false;
                    String stepNr = "";
                    for (int s = 0; s < schritteProProzess; s++) {
                        int startzeit = start[p][s].getValue();
                        int endzeit = startzeit + dauer[p][s].getValue();
                        if (t >= startzeit && t < endzeit) {
                            aktiv = true;
                            stepNr = ""+s;
                            for (int i = 0; i<kapazitaetWerte[p][s]; i++ ) {
                                String processName = ""+getProcessLabel( p);
                                field[capProTick+i][t] = "["+processName+(s+1)+"]";
                            }

                            capProTick = capProTick + kapazitaetWerte[p][s];
                            break;
                        }
                    }
                    line.append(aktiv ? ("#"+stepNr) : "_.");
                   // System.out.println(line);
                }

            }
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[i].length; j++) {


                        System.out.print(field[i][j]+"" );

                }
                System.out.println();
            }
            System.out.println();
        }

        if (numberOfFoundSolutions > 0) {
            /*
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[i].length; j++) {
                    System.out.print(field[i][j]+"" );
                }
                System.out.println();
            }
            System.out.println();

             */
        } else {
            System.out.println("Keine Lösung gefunden.");
        }
    }

    public static void initEmptyField(String[][] field) {
        for (int i = 0; i < MAX_CAPACITY; i++) {
            for (int j = 0; j < MAX_TIME; j++) {
                field[i][j] = "[__]";
            }
        }
    }
}
