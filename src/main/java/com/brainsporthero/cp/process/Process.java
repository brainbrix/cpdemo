package com.brainsporthero.cp.process;

import java.util.ArrayList;
import java.util.List;

public class Process {

    String name;
    List<Task> tasks = new ArrayList<Task>();
    public int lastTick = 0;

    public Process(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        task.setProcess(this);
        tasks.add(task);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        tasks.forEach(task -> sb.append(task.toString()));
        return sb.toString();
    }

    public int[] getDurationForTasks() {
        int[] durations = new int[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            durations[i] = task.getDuration();
        }
        return durations;
    }

    public int[] getCapacitiesForTasks() {
        int[] capacities = new int[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            capacities[i] = task.getResource();
        }
        return capacities;
    }

}
