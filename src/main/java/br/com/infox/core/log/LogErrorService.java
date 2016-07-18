package br.com.infox.core.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.FacesException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;

import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.log.LogErro;
import br.com.infox.epp.log.StatusLog;
import br.com.infox.epp.log.rest.LogRest;
import br.com.infox.epp.log.rest.dto.LogDTO;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.ws.factory.RestClientFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LogErrorService extends PersistenceController {
    
    public final static String ERROR_MESSAGE_FORMAT = "Usuario: %s , Localizacao: %s , Perfil: %s \n";
    public static final String LOG_ERRO_FILE_NAME = "logErro.log";
    private static final String LOG_ERRO_TEMP_FILE_NAME = "logErroTmp.log";
    
    @Inject
    private ApplicationServerService applicationServerService;
    @Inject
    private LogErroSearch logErroSearch;
    @Inject
    private ParametroDAO parametroDAO;
    
    private void saveLog(LogErro logErro) {
        try {
            getEntityManager().persist(logErro);
            getEntityManager().flush();
        } catch (Exception e) {
            logErro.setId(null);
            String logPath = applicationServerService.getLogDir();
            if (!StringUtil.isEmpty(logPath)) {
                File dir = new File(applicationServerService.getLogDir());
                File file = new File(dir, LOG_ERRO_FILE_NAME);
                try ( FileWriter fileWriter = new FileWriter(file, true)){
                    String data = new GsonBuilder().create().toJson(logErro) + "\n";
                    fileWriter.write(data, 0, data.getBytes().length);
                    fileWriter.flush();
                } catch (Exception e1) { // do nothing
                }
            }
            throw e;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void send(LogErro logErro) {
        Parametro urlEnvio = parametroDAO.getParametroByNomeVariavel(Parametros.URL_SERVICO_ENVIO_LOG_ERRO.getLabel());
        Parametro client = parametroDAO.getParametroByNomeVariavel(Parametros.CODIGO_CLIENTE_ENVIO_LOG.getLabel());
        Parametro pass = parametroDAO.getParametroByNomeVariavel(Parametros.PASSWORD_CLIENTE_ENVIO_LOG.getLabel());
        if (urlEnvio == null || StringUtil.isEmpty(urlEnvio.getValorVariavel())) {
            throw new BusinessException("URL de envio não configurada. Favor configurar parâmetro " + Parametros.URL_SERVICO_ENVIO_LOG_ERRO.getLabel());
        }
        if (client == null || StringUtil.isEmpty(client.getValorVariavel())) {
            throw new BusinessException("Cliente para envio não configurado. Favor configurar parâmetro " + Parametros.CODIGO_CLIENTE_ENVIO_LOG.getLabel());
        }
        if (pass == null || StringUtil.isEmpty(pass.getValorVariavel())) {
            throw new BusinessException("Password de cliente para envio não configurado. Favor configurar parâmetro " + Parametros.PASSWORD_CLIENTE_ENVIO_LOG.getLabel());
        }
        StatusLog status = logErro.getStatus();
        try {
            LogRest logRest = RestClientFactory.create(urlEnvio.getValorVariavel(), LogRest.class);
            LogDTO logDTO = createLogDto(logErro);
            logRest.getLogResource(client.getValorVariavel(), pass.getValorVariavel()).add(logDTO);
            logErro.setStatus(StatusLog.ENVIADO);
            logErro.setErroEnvio(null);
            logErro.setDataEnvio(DateTime.now().toDate());
            atualizarLogErro(logErro);
        } catch (Exception e) {
            logErro.setStatus(status);
            logErro.setErroEnvio(e.getMessage());
            atualizarLogErro(logErro);
            throw new BusinessException(e.getMessage());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAndSend(LogErro logErro) {
        saveLog(logErro);
        send(logErro);
    }

    private LogDTO createLogDto(LogErro logErro) {
        LogDTO logDTO = new LogDTO();
        logDTO.setCodigo(logErro.getCodigo().toString());
        logDTO.setData(logErro.getData());
        logDTO.setInstancia(logErro.getInstancia());
        logDTO.setStackTrace(logErro.getStacktrace());
        return logDTO;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atualizarRegistroLogErro() throws IOException {
        File fileLogErro = new File(applicationServerService.getLogDir(), LOG_ERRO_FILE_NAME);
        File fileLogErroTemp = new File(applicationServerService.getLogDir(), LOG_ERRO_TEMP_FILE_NAME);
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileLogErro));
            bufferedWriter = new BufferedWriter(new FileWriter(fileLogErroTemp, true));
            String currentLine = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                LogErro logErro = new GsonBuilder().create().fromJson(currentLine.trim(), LogErro.class);
                try {
                    getEntityManager().persist(logErro);
                    getEntityManager().flush();
                } catch (Exception e) {
                    bufferedWriter.write(currentLine, 0, currentLine.getBytes().length);
                    bufferedWriter.flush();
                }
            }
        } finally {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            fileLogErro.delete();
            fileLogErroTemp.renameTo(fileLogErro);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atualizarLogErro(LogErro logErro) {
        getEntityManager().merge(logErro);
        getEntityManager().flush();
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sendAllPendentesEnvio() {
        List<LogErro> logErroPendenteEnvio = logErroSearch.listAllPendentesEnvio();
        for (LogErro logErro : logErroPendenteEnvio) {
           try {
               send(logErro);
           } catch (Exception e) {
           }
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogErro log(Throwable exception) {
        Throwable handledException = getHandledException(exception);
        String codigo = UUID.randomUUID().toString().replace("-", "");
        boolean enviarLog = "true".equals(Parametros.IS_ATIVO_ENVIO_LOG_AUTOMATICO.getValue());
        LogErro logErro = new LogErro();
        logErro.setCodigo(codigo);
        logErro.setData(DateTime.now().toDate());
        logErro.setInstancia(applicationServerService.getInstanceName() == null ? Thread.currentThread().getName() : applicationServerService.getInstanceName());
        logErro.setStatus(enviarLog ? StatusLog.PENDENTE : StatusLog.NENVIADO);
        logErro.setStacktrace(getStacktrace(handledException));
        saveLog(logErro);
        if (enviarLog) {
            try {
                send(logErro);
            } catch (Exception e) {}
        }
        return logErro;
    }
    
    private String getStacktrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        exception.printStackTrace(printWriter);
        return getUserAttributes() + sw.toString();
    }
    
    private String getUserAttributes() {
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
        String codigoLocalizacao = localizacao == null ? "" : localizacao.getCodigo();
        String login = usuarioPerfil == null ? Thread.currentThread().getName() : usuarioPerfil.getUsuarioLogin().getLogin();
        String perfil = usuarioPerfil == null ? "" : usuarioPerfil.getPerfilTemplate().getCodigo();
        return String.format(ERROR_MESSAGE_FORMAT, login, codigoLocalizacao, perfil);
    }
    
    private Throwable getHandledException(Throwable throwable) {
        if ((throwable instanceof EJBException) || (throwable instanceof FacesException) || (throwable instanceof ServletException)) {
            return getHandledException(throwable.getCause());
        } else {
            return throwable;
        }
    }
}
