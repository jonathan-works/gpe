package br.com.infox.epp.painel.caixa;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;

@AutoCreate
@Name(CaixaDAO.NAME)
public class CaixaDAO extends DAO<Caixa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "caixaDAO";
    
    public Caixa getCaixaByIdTarefaAndIdNodeAnterior(Integer idTarefa, Integer idNodeAnterior) {
    	Map<String, Object> params = new HashMap<>(2);
    	params.put(CaixaQuery.PARAM_ID_TAREFA, idTarefa);
    	params.put(CaixaQuery.PARAM_ID_NODE_ANTERIOR, idNodeAnterior);
    	return getNamedSingleResult(CaixaQuery.CAIXA_BY_ID_TAREFA_AND_ID_NODE_ANTERIOR, params);
    }
    
}
