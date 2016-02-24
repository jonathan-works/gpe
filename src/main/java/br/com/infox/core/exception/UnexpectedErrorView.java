package br.com.infox.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;

import br.com.infox.core.log.ErrorLogService;
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
    private ErrorLogService errorLogService;
    @Inject
    private ApplicationServerService applicationServerService;
    
    private boolean managed;
    private LogErro logErro;
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void sendErrorLog() {
        Exception handledException = ComponentUtil.getComponent("org.jboss.seam.handledException");
        if (handledException == null) return;
        String ativo = Parametros.IS_ATIVO_ENVIO_LOG_AUTOMATICO.getValue();
        logErro = new LogErro();
        logErro.setCodigo(String.valueOf(DateTime.now().toDate().getTime()));
        logErro.setData(DateTime.now().toDate());
        logErro.setInstancia(applicationServerService.getInstanceName());
        logErro.setStacktrace(getStacktrace(handledException));
        if ("true".equals(ativo)) {
            logErro.setStatus(StatusLog.PENDENTE);
            String remote = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
            errorLogService.gravarLog(logErro);
            errorLogService.send(logErro, remote);
        } else {
            logErro.setStatus(StatusLog.NENVIADO);
            errorLogService.gravarLog(logErro);
        }
        this.managed = true;
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

    public LogErro getLogErro() {
        return logErro;
    }

    public boolean isManaged() {
        return managed;
    }

}
