package br.com.infox.ibpm.variable.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import static br.com.infox.ibpm.variable.query.DominioVariavelTarefaQuery.*;

@Name(DominioVariavelTarefaDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DominioVariavelTarefaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "dominioVariavelTarefaDAO";

	public DominioVariavelTarefa getDominioVariavelTarefa(Integer id) {
		Map<String, Object> params = new HashMap<>();
		params.put(PARAM_ID, id);
		return getNamedSingleResult(NAMED_QUERY_GET_DOMINIO, params);
	}
}
