package com.jdddata.middleware.databus.canal;

public class TaskReferrer {
    private Thread thread;

    private DestinationTask task;

    public TaskReferrer(Thread t, DestinationTask d) {
        this.thread = t;
        this.task = d;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public DestinationTask getTask() {
        return task;
    }

    public void setTask(DestinationTask task) {
        this.task = task;
    }
}
