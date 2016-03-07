package br.com.infox.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;

import br.com.infox.core.log.LogErrorService;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.log.LogErro;
import br.com.infox.epp.log.StatusLog;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.util.ComponentUtil;

@Named
@RequestScoped
public class UnexpectedErrorView {
    
    public final static String ERROR_MESSAGE_FORMAT = "Usuario: %s , Localizacao: %s , Perfil: %s \n";
    
    @Inject
    private LogErrorService errorLogService;
    @Inject
    private ApplicationServerService applicationServerService;
    
    private String codigoErro;
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void sendErrorLog() {
        codigoErro = UUID.randomUUID().toString().replace("-", "");
        Logger.getLogger(UnexpectedErrorView.class.getName()).log(Level.SEVERE, codigoErro);
        Exception handledException = ComponentUtil.getComponent("org.jboss.seam.handledException");
        Exception caughtException = ComponentUtil.getComponent("org.jboss.seam.caughtException"); 
        if (handledException == null) return;
        String ativo = Parametros.IS_ATIVO_ENVIO_LOG_AUTOMATICO.getValue();
        if ("true".equals(ativo)) {
            LogErro logErro = creteLogErro(handledException, caughtException, StatusLog.PENDENTE);
            errorLogService.saveLog(logErro);
            errorLogService.send(logErro);
        } else {
            LogErro logErro = creteLogErro(handledException, caughtException, StatusLog.NENVIADO);
            errorLogService.saveLog(logErro);
        }
    }

    private LogErro creteLogErro(Exception handledException, Exception caughtException, StatusLog statusLog) {
        LogErro logErro = new LogErro();
        logErro.setCodigo(codigoErro);
        logErro.setData(DateTime.now().toDate());
        logErro.setInstancia(applicationServerService.getInstanceName());
        String stackTrace = getStacktrace(handledException);
        if (caughtException != null) {
            stackTrace += getStacktrace(caughtException);
        }
        logErro.setStacktrace(stackTrace);
        logErro.setStatus(statusLog);
        return logErro;
    }

    private String getStacktrace(Exception handledException) {
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        handledException.printStackTrace(printWriter);
        return getUserAttributes() + sw.toString();
    }
    
    private String getUserAttributes() {
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        return String.format(ERROR_MESSAGE_FORMAT, usuarioPerfil.getUsuarioLogin().getLogin(), localizacao.getCodigo(), usuarioPerfil.getPerfilTemplate().getCodigo());
    }

    public String getCodigoErro() {
        return codigoErro;
    }

}
