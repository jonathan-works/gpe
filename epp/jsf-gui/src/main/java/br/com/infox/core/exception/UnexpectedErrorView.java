package br.com.infox.core.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.log.LogErrorService;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.log.LogErro;
import br.com.infox.seam.util.ComponentUtil;

@Named
@RequestScoped
public class UnexpectedErrorView {
    
    private static final Logger LOG = Logger.getLogger(UnexpectedErrorView.class.getName());
    
    @Inject
    private LogErrorService errorLogService;
    
    private String codigoErro;
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void sendErrorLog() {
        Exception caughtException = ComponentUtil.getComponent("org.jboss.seam.caughtException");
        if (caughtException != null) {
            LogErro logErro = errorLogService.log(caughtException);
            codigoErro = logErro.getCodigo();
            LOG.log(Level.SEVERE, codigoErro, caughtException);
        }
    }

    public String getCodigoErro() {
        return codigoErro;
    }

}
