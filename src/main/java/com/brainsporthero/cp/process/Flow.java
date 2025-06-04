package com.brainsporthero.cp.process;

import static com.brainsporthero.cp.process.Const.MAX_CAPACITY;
import static com.brainsporthero.cp.process.Const.MAX_TIME;

public class Flow {


    Task[][] field = new Task[MAX_CAPACITY][MAX_TIME];
    int maxTime =0;

    public boolean checkFit( Task task, int x, int y) {
        for (int i = 0; i < task.getResource(); i++) {
            for (int j = 0; j < task.getDuration(); j++) {
                if (!validPos(x+i, y+j) || (field[x+i][y+j] != null)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validPos(int x, int y) {
        return (x >= 0 && x < MAX_CAPACITY && y >= 0 && y < MAX_TIME);
    }

    public void add( Task task) {

        int currentTick = task.getProcess().lastTick;
        int pos = 0;


        while (!(checkFit(task, pos, currentTick))) {

            if (pos >= field.length) {
                currentTick++;
                pos = 0;
            } else {
                pos++;
            }

        }

        for (int i = 0; i < task.getResource(); i++) {
            for (int j = 0; j < task.getDuration(); j++) {
                field[pos+i][currentTick+j] = task;
                if ((currentTick+j+1) > maxTime) {
                    maxTime = currentTick+j+1;
                }
            }
        }
        task.getProcess().lastTick = currentTick+task.getDuration();
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void print() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                Task t = field[i][j];
                if (t != null) {
                    System.out.print("[" + field[i][j].getProcess().name + field[i][j].id + "]");
                } else {
                    System.out.print("[__]");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
