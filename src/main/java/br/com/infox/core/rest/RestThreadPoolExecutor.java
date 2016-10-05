package br.com.infox.core.rest;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;

public interface RestThreadPoolExecutor {
    String NAME ="restThreadPoolExecutor"; 
    int BASE_MAXIMUM_POOL_SIZE = 10;
    
    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Runnable task, T result);

    Future<?> submit(Runnable task);

    boolean isShutdown();

    boolean isTerminated();

    int getActiveCount();

    void setMaximumPoolSize(int maximumPoolSize);
    
    int getMaximumPoolSize();

    long getCompletedTaskCount();

    void setRejectedExecutionHandler(RejectedExecutionHandler handler);
}
