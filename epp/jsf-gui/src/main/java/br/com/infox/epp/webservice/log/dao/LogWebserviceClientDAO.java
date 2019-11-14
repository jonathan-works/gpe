package br.com.infox.epp.webservice.log.dao;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;
import br.com.infox.epp.webservice.log.query.LogWebserviceClientQuery;

@Stateless
@AutoCreate
@Name(LogWebserviceClientDAO.NAME)
public class LogWebserviceClientDAO extends DAO<LogWebserviceClient> {
	
    public static final String NAME = "logWebserviceClientDAO";
	private static final long serialVersionUID = 1L;
	
	public String getRequisicaoFromLog(Long idLog) {
		Map<String, Object> params = new HashMap<>();
		params.put(LogWebserviceClientQuery.PARAM_ID_LOG, idLog);
		return getNamedSingleResult(LogWebserviceClientQuery.GET_REQUISICAO_FROM_LOG, params);
	}
	
	public String getInformacoesAdicionaisFromLog(Long idLog) {
		Map<String, Object> params = new HashMap<>();
		params.put(LogWebserviceClientQuery.PARAM_ID_LOG, idLog);
		return getNamedSingleResult(LogWebserviceClientQuery.GET_INFORMACOES_ADICIONAIS_FROM_LOG, params);
	}
	
	public String getRespostaFromLog(Long idLog) {
		Map<String, Object> params = new HashMap<>();
		params.put(LogWebserviceClientQuery.PARAM_ID_LOG, idLog);
		return getNamedSingleResult(LogWebserviceClientQuery.GET_RESPOSTA_FROM_LOG, params);
	}
}
