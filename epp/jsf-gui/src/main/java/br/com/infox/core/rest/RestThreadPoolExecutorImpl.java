package br.com.infox.core.rest;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;

@Singleton
public class RestThreadPoolExecutorImpl implements Serializable, RestThreadPoolExecutor {

    
    private static final long serialVersionUID = 1L;
    private ThreadPoolExecutor threadPoolExecutor;
    @Inject
    private ParametroManager parametroManager;
    
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
        SynchronousQueue<Runnable> workQueue = new SynchronousQueue<Runnable>(true);
        threadPoolExecutor = new ThreadPoolExecutor(getCorePoolSize(), getParameteredMaximumPoolSize(), getKeepAliveTime(), TimeUnit.NANOSECONDS, workQueue);
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                super.rejectedExecution(r, executor);
                throw new WebApplicationException(getRejectionResponse());
            }
        });
    }

    /**
     * Método que recupera valor dos parâmetros. protected para permitir testes unitários desta implementação
     * @param parametro Parâmetro cujo valor será recuperado
     * @return Valor do parâmetro
     */
    protected String getValor(Parametros parametro){
        return parametroManager.getValorParametro(parametro.getLabel());
    }
    
    /**
     * Método auxiliar para impedir NumberFormat exception quando o valor do parâmetro for nulo
     * @param parametro parâmetro cujo valor será convertido
     * @return Valor do parâmetro convertido para inteiro, ou null
     */
    private Integer getIntValue(Parametros parametro){
        String value = getValor(parametro);
        return value == null || !value.trim().matches("^[0-9]+$") ? null : Integer.valueOf(value,10);
    }
    
    /**
     * Método auxiliar para impedir NumberFormat exception quando o valor do parâmetro for nulo
     * @param parametro parâmetro cujo valor será convertido
     * @return Valor do parâmetro convertido para inteiro, ou null
     */
    private Long getLongValue(Parametros parametro){
        String value = getValor(parametro);
        
        return value == null || !value.trim().matches("^[0-9]+$")? null : Long.valueOf(value,10);
    }
    
    private long getKeepAliveTime() {
        return ObjectUtils.defaultIfNull(getLongValue(Parametros.REST_THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME), Long.MAX_VALUE);
    }

    private int getParameteredMaximumPoolSize() {
        return ObjectUtils.defaultIfNull(getIntValue(Parametros.REST_THREAD_POOL_EXECUTOR_MAXIMUM_POOL_SIZE), BASE_MAXIMUM_POOL_SIZE);
    }

    private int getCorePoolSize() {
        return ObjectUtils.defaultIfNull(getIntValue(Parametros.REST_THREAD_POOL_EXECUTOR_CORE_POOL_SIZE), 0);
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
