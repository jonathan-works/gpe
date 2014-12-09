package br.com.infox.epp.julgamento.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.query.SessaoJulgamentoQuery;

@AutoCreate
@Name(SessaoJulgamentoDAO.NAME)
public class SessaoJulgamentoDAO extends DAO<SessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoDAO";
	
	public boolean existeSessaoJulgamentoComSalaEHorario(Sala sala, Date dataInicio, Date dataFim) {
		Map<String, Object> params = new HashMap<>();
		params.put(SessaoJulgamentoQuery.PARAM_SALA, sala);
		params.put(SessaoJulgamentoQuery.PARAM_DATA_INICIO, dataInicio);
		params.put(SessaoJulgamentoQuery.PARAM_DATA_FIM, dataFim);
		return (long) getNamedSingleResult(SessaoJulgamentoQuery.EXISTE_SESSAO_COM_SALA_E_HORARIO, params) > 0;
	}
	

}
