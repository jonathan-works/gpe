package br.com.infox.core.rest;

import java.io.Serializable;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@RestThreadPool
@Interceptor
public class RestThreadPoolInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static long activeCount=0;
    
    @Inject
    private ParametroManager parametroManager;
    
    
    public static synchronized long incrementActive(){
        return ++activeCount;
    }
    public static synchronized long decrementActive(){
        return --activeCount;
    }
    
    public static synchronized long getActive(){
        return activeCount;
    }
    
    private Integer getIntValue(Parametros parametro){
        Parametro p = parametroManager.getParametro(parametro.getLabel());
        String value = p == null ? null : p.getValorVariavel();
        return value == null || !value.trim().matches("^[0-9]+$") ? 10 : Integer.valueOf(value,10);
    }
    
    @AroundInvoke
    public Object handleException(final InvocationContext context) throws Exception {
        if (RestThreadPoolInterceptor.getActive() < getIntValue(Parametros.REST_THREAD_POOL_EXECUTOR_MAXIMUM_POOL_SIZE)){
            incrementActive();
            Object result = context.proceed();
            decrementActive();
            return result;
        }
        throw new WebApplicationException(createRejectionResponse());
    }

    
    private Response createRejectionResponse(){
        return Response.status(429).entity(getRejectionResponseEntity()).build();
    }
    
    private String getRejectionResponseEntity(){
        JsonObject errorObject = new JsonObject();
        errorObject.addProperty("errorMessage", InfoxMessages.getInstance().get("restPool.executionRejected"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(errorObject);
    }
}
