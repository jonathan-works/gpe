package br.com.infox.core.rest;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.infox.core.messages.InfoxMessages;

@Startup
@Singleton
public class RestThreadPoolExecutorImpl implements Serializable, RestThreadPoolExecutor {

    
    private static final long serialVersionUID = 1L;
    private BlockingQueue<Runnable> queue;
    private ThreadPoolExecutor threadPoolExecutor;
    
    private Response getRejectionResponse(){
        return Response.status(429).entity(getRejectionResponseEntity()).build();
    }
    
    private String getRejectionResponseEntity(){
        JsonObject errorObject = new JsonObject();
        errorObject.addProperty("errorMessage", InfoxMessages.getInstance().get("restPool.executionRejected"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(errorObject);
    }
    
    @PostConstruct
    protected void init(){
//        queue = new ArrayBlockingQueue<>(BASE_MAX_QUEUE, true);
        queue = new SynchronousQueue<>(true);
//        queue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(0, BASE_MAX_QUEUE, Long.MAX_VALUE, TimeUnit.NANOSECONDS, queue);
        
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() {
            
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                super.rejectedExecution(r, executor);
                throw new WebApplicationException(getRejectionResponse());
            }
        });
    }
    
    public <T> Future<T> submit(Callable<T> task){
        return threadPoolExecutor.submit(task);
    }
    
    public <T> Future<T> submit(Runnable task, T result){
        return threadPoolExecutor.submit(task, result);
    }
    
    public Future<?> submit(Runnable task){
        return threadPoolExecutor.submit(task);
    }

    @Override
    public int getActiveCount() {
        return threadPoolExecutor.getActiveCount();
    }
    
    @Override
    public long getCompletedTaskCount() {
        return threadPoolExecutor.getCompletedTaskCount();
    }
    
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
    }
    
    @Override
    public int getMaximumPoolSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }
    
    @Override
    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return threadPoolExecutor.isTerminated();
    }
    
    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        threadPoolExecutor.setRejectedExecutionHandler(handler);
        
    }
    
}
