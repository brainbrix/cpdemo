package com.brainsporthero.cp.process;

public class Task {
    String name;
    int resource;
    int duration;
    int id;

    Process process;

    static int globalTaskId = 0;

    public static int getNextId() {
        globalTaskId = globalTaskId + 1;
        return globalTaskId;
    }

    public Task(String name, int resource, int duration) {
        this.name = name;
        this.resource = resource;
        this.duration = duration;
        id = getNextId();
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task: "+name+"("+id+") resource: "+resource+" duration: "+duration+"\n");
        return sb.toString();
    }
}
