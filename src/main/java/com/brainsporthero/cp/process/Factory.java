package com.brainsporthero.cp.process;

import java.util.Random;

import static com.brainsporthero.cp.process.Const.*;

public class Factory {

    static private String PROCESS_NAME = "ABCDEFGHIJLKMNOPQRSTUVWXYZabcdefeghijklmnopqrstuvwxyz1234567890";

    public static String getProcessLabel( int position ) {
        return ""+PROCESS_NAME.charAt(position % PROCESS_NAME.length());
    }

    static int countOfProcesses = 0;

    static Random random = new Random();

    private Factory() {
        // Ignore
    }

    public static Task createRandomTask() {

        Task task = new Task( "Task " + random.nextInt(100), random.nextInt(MIN_TASK_RESOURCES,MAX_TASK_RESOURCES), random.nextInt(MIN_TASK_DURATION,MAX_TASK_DURATION));
        return task;
    }

    public static Process createRandomProcessDefinition() {
        Process processDefinition = new Process(getProcessLabel(countOfProcesses));
        countOfProcesses++;
        for (int i = 0; i < NUMBER_OF_TASKS_PER_PROCESS; i++) {
            Task task = createRandomTask();
            task.setId( i+1);
            processDefinition.addTask(task);
        }
        return processDefinition;
    }
}
