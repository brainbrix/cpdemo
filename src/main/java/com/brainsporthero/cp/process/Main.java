package com.brainsporthero.cp.process;

import com.brainsporthero.cp.process.choco.ProzessStarter;

import static com.brainsporthero.cp.process.Const.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        Flow flow = new Flow();

        int[][] durations = new int[NUMBER_OF_PROCESSES][NUMBER_OF_TASKS_PER_PROCESS];
        int[][] caps = new int[NUMBER_OF_PROCESSES][NUMBER_OF_TASKS_PER_PROCESS];

        for (int i = 1; i <= NUMBER_OF_PROCESSES; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            Process processDefinition = Factory.createRandomProcessDefinition();
            for (int j = 0; j <= (NUMBER_OF_TASKS_PER_PROCESS-1); j++) {
                flow.add( processDefinition.getTasks().get(j));
                if (LOG_STEPS) {
                    flow.print();
                }
            }
            durations[i-1] = processDefinition.getDurationForTasks();
            caps[i-1] = processDefinition.getCapacitiesForTasks();
        }

        flow.print();
        System.out.println( "MaxTime: " + flow.getMaxTime() );

        ProzessStarter.kapazitaetWerte = caps;
        ProzessStarter.dauerWerte = durations;
        ProzessStarter ps = new ProzessStarter();
        ps.start();
    }
}