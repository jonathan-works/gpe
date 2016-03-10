package br.com.infox.core.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.core.util.StringUtil;
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
public class LogErrorService {
    
    public static final String LOG_ERRO_FILE_NAME = "logErro.log";
    private static final String LOG_ERRO_TEMP_FILE_NAME = "logErroTmp.log";
    
    @Inject
    private ApplicationServerService applicationServerService;
    @Inject
    private LogErroSearch logErroSearch;
    @Inject
    private ParametroDAO parametroDAO;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveLog(LogErro logErro) {
        try {
            getEntityManager().persist(logErro);
            getEntityManager().flush();
        } catch (Exception e) {
            logErro.setId(null);
            File dir = new File(applicationServerService.getLogDir());
            File file = new File(dir, LOG_ERRO_FILE_NAME);
            try ( FileWriter fileWriter = new FileWriter(file, true)){
                String data = new GsonBuilder().create().toJson(logErro) + "\n";
                fileWriter.write(data, 0, data.getBytes().length);
                fileWriter.flush();
            } catch (IOException e1) { // do nothing
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

}
