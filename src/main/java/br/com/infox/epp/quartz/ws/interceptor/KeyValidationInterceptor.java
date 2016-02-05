package br.com.infox.epp.quartz.ws.interceptor;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import br.com.infox.core.report.RequestInternalPageService;

@KeyValidation
@Interceptor
public class KeyValidationInterceptor implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private RequestInternalPageService requestInternalPageService;
    @Inject @RequestScoped
    private ServletRequest request;

    @AroundInvoke
    public Object validateRequestInternal(InvocationContext invocationContext) throws Exception {
        String key = ((HttpServletRequest) request).getHeader("key");
        if (!requestInternalPageService.isValid(key)) {
            throw new WebApplicationException(401);
        }
        return invocationContext.proceed();
    }

}
