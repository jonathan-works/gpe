package br.com.infox.epp.julgamento.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.StatusSessaoJulgamento;
import br.com.infox.epp.julgamento.query.StatusSessaoJulgamentoQuery;

@AutoCreate
@Name(StatusSessaoJulgamentoDAO.NAME)
public class StatusSessaoJulgamentoDAO extends DAO<StatusSessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "statusSessaoJulgamentoDAO";
	
	public StatusSessaoJulgamento getStatusSessaoJulgamentoByNome(String nome) {
	    Map<String, Object> params = new HashMap<>(1);
	    params.put(StatusSessaoJulgamentoQuery.PARAM_NOME_STATUS, nome);
	    return getNamedSingleResult(StatusSessaoJulgamentoQuery.GET_STATUS_SESSAO_JULGAMENTO_BY_NOME, params);
	}

}
