package br.com.infox.epp.access.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.query.EstruturaQuery;

@Name(EstruturaDAO.NAME)
@AutoCreate
public class EstruturaDAO extends DAO<Estrutura> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaDAO";
    
    public List<Estrutura> getEstruturasDisponiveis() {
        return getNamedResultList(EstruturaQuery.ESTRUTURAS_DISPONIVEIS);
    }
    
    public Estrutura getEstruturaByNome(String nome){
    	Map<String, Object> params = new HashMap<>();
    	params.put(EstruturaQuery.PARAM_NOME, nome);
    	return getNamedSingleResult(EstruturaQuery.ESTRUTURA_BY_NOME, params);
    }
}
