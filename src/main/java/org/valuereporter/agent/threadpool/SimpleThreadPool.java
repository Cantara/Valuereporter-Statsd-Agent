package org.valuereporter.agent.threadpool;

import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 21.07.15.
 */
public class SimpleThreadPool {
    private static final Logger log = getLogger(SimpleThreadPool.class);

    public static void main(String[] args) {
//        ExecutorService executor = Executors.newFixedThreadPool(5);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        for (int i = 1; i < 11; i++) {
            Runnable worker = new WorkerThread(" " + i);
//            executor.execute(worker);
            log.debug("Active Count {}, maxCount {}", executor.getActiveCount(), executor.getMaximumPoolSize());
            if (executor.getActiveCount() < executor.getMaximumPoolSize()) {
                executor.submit(worker);
            }else {
                log.info("No threads available. Will discard worker {}", i);
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Unexptected Interupt");
        }
//        while (!executor.isTerminated()) {
//        }
        log.debug("Finished all threads");
    }

}
