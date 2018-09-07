package org.valuereporter.agent.threadpool;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Example Implementation from http://www.javacodegeeks.com/2013/01/java-thread-pool-example-using-executors-and-threadpoolexecutor.html
 * Created by baardl on 21.07.15.
 */
public class WorkerThread implements Runnable {
    private static final Logger log = getLogger(WorkerThread.class);

    private String command;

    public WorkerThread(String s){
        this.command=s;
    }

    @Override
    public void run() {
        log.debug("{}  Start. Command = {}", Thread.currentThread().getName(), command);
        processCommand();
        log.debug(" {} End.",Thread.currentThread().getName());
    }

    private void processCommand() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return this.command;
    }
}