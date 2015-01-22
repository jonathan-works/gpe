package br.com.infox.epp.julgamento.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.query.SessaoJulgamentoQuery;

@AutoCreate
@Name(SessaoJulgamentoDAO.NAME)
public class SessaoJulgamentoDAO extends DAO<SessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoDAO";
	
	public boolean existeSessaoJulgamentoComSalaEHorario(SessaoJulgamento sessaoJulgamento) {
		Map<String, Object> params = new HashMap<>(4);
		params.put(SessaoJulgamentoQuery.PARAM_SALA, sessaoJulgamento.getSala());
		params.put(SessaoJulgamentoQuery.PARAM_DATA, sessaoJulgamento.getData());
		params.put(SessaoJulgamentoQuery.PARAM_HORA_INICIO, sessaoJulgamento.getHoraInicio());
		params.put(SessaoJulgamentoQuery.PARAM_HORA_FIM, sessaoJulgamento.getHoraFim());
		return (long) getNamedSingleResult(SessaoJulgamentoQuery.EXISTE_SESSAO_COM_SALA_E_HORARIO, params) > 0;
	}
	

}
