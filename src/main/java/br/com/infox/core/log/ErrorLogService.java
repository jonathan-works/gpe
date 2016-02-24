package br.com.infox.core.log;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.log.LogErro;
import br.com.infox.epp.log.StatusLog;
import br.com.infox.epp.log.rest.LogRest;
import br.com.infox.epp.log.rest.dto.LogDTO;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.ws.factory.RestClientFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ErrorLogService {
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarLog(LogErro logErro) {
        getEntityManager().persist(logErro);
        getEntityManager().flush();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void send(LogErro logErro, String endereco) {
        String urlEnvio = Parametros.URL_SERVICO_ENVIO_LOG_ERRO.getValue();
        String client = Parametros.CODIGO_CLIENTE_ENVIO_LOG.getValue();
        String pass = Parametros.PASSWORD_CLIENTE_ENVIO_LOG.getValue();
        if (StringUtil.isEmpty(urlEnvio)) {
            throw new BusinessException("URL de envio não configurada. Favor configurar parâmetro " + Parametros.URL_SERVICO_ENVIO_LOG_ERRO.getLabel());
        }
        if (StringUtil.isEmpty(client)) {
            throw new BusinessException("Cliente para envio não configurado. Favor configurar parâmetro " + Parametros.CODIGO_CLIENTE_ENVIO_LOG.getLabel());
        }
        if (StringUtil.isEmpty(pass)) {
            throw new BusinessException("Password de cliente para envio não configurado. Favor configurar parâmetro " + Parametros.PASSWORD_CLIENTE_ENVIO_LOG.getLabel());
        }
        try {
            LogRest logRest = RestClientFactory.create(urlEnvio, LogRest.class);
            LogDTO logDTO = createLogDto(logErro, endereco);
            logRest.getLogResource(client, pass).add(logDTO);
            logErro.setStatus(StatusLog.ENVIADO);
            logErro.setErroEnvio(null);
            logErro.setDataEnvio(DateTime.now().toDate());
            atualizarLogErro(logErro);
        } catch (Exception e) {
            logErro.setStatus(StatusLog.PENDENTE);
            logErro.setErroEnvio(e.getMessage());
            atualizarLogErro(logErro);
            throw new BusinessException(e.getMessage());
        }
    }

    private LogDTO createLogDto(LogErro logErro, String endereco) {
        LogDTO logDTO = new LogDTO();
        logDTO.setCodigo(logErro.getCodigo().toString());
        logDTO.setData(logErro.getData());
        logDTO.setInstancia(logErro.getInstancia());
        logDTO.setStackTrace(logErro.getStacktrace());
        logDTO.setEndereco(endereco);
        return logDTO;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atualizarLogErro(LogErro logErro) {
        getEntityManager().merge(logErro);
        getEntityManager().flush();
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
