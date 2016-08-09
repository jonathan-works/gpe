package br.com.infox.core.rest;

import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestThreadPoolExecutorTest {
    
    private static RestThreadPoolExecutor executor;
    
    @Before
    public void init(){
        RestThreadPoolExecutorImpl executorImpl = new RestThreadPoolExecutorImpl();
        executorImpl.init();
        executor = executorImpl;
    }
    
    
    private void execute(final Runnable task, int value){
        createExecutionThread(task, value).start();
    }


    private Thread createExecutionThread(final Runnable task, final int value) {
        return new Thread(){
            @Override
            public void run() {
                Future<Integer> future = executor.submit(task, value);
                while(!future.isDone() && !future.isCancelled()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
    }
    
    private Runnable getTestRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void waitForCompletion() {
        while(executor.getActiveCount()>0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Test
    public void testStandardExecution(){
        for(int i=0;i<RestThreadPoolExecutor.BASE_MAX_QUEUE+5;i++){
            final int val = i;
            execute(getTestRunnable(), val);
        }
        waitForCompletion();
        Assert.assertEquals("Total executions error ",10L, executor.getCompletedTaskCount());
    }
    
    @Test
    public void testSetMaximumPoolSize(){
        executor.setMaximumPoolSize(5);
        for(int i=0;i<10;i++){
            final int val = i;
            execute(getTestRunnable(), val);
        }
        waitForCompletion();
        Assert.assertEquals("Total executions error ",5L, executor.getCompletedTaskCount());
    }
    
}
