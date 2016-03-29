package br.com.infox.core.exception;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.log.LogErrorService;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.seam.util.ComponentUtil;

@Named
@RequestScoped
public class UnexpectedErrorView {
    
    @Inject
    private LogErrorService errorLogService;
    
    private String codigoErro;
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void sendErrorLog() {
        Exception handledException = ComponentUtil.getComponent("org.jboss.seam.handledException");
        Exception caughtException = ComponentUtil.getComponent("org.jboss.seam.caughtException"); 
        codigoErro = errorLogService.log(handledException, caughtException).getCodigo();
    }

    public String getCodigoErro() {
        return codigoErro;
    }

}
