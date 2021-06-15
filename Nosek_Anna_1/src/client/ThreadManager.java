package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager {

    private ExecutorService threadPool;
    private boolean shouldStop;

    public ThreadManager(int threadNo){
        this.threadPool = Executors.newFixedThreadPool(threadNo);
        this.shouldStop = false;
    }

    public void startRunnable(Runnable runnable){
        threadPool.submit(runnable);
    }

    private synchronized void setShouldStop(){
        this.shouldStop = true;
    }

    public synchronized boolean shouldStop(){
        return shouldStop;
    }

    public void shutdown(){
        threadPool.shutdown();
    }

    public void shutdownNow(){
        setShouldStop();
        threadPool.shutdownNow();
    }

    public void awaitTermination() throws InterruptedException {
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

}
