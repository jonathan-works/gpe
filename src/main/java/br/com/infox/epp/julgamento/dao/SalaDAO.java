package br.com.infox.epp.julgamento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.query.SalaQuery;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@AutoCreate
@Name(SalaDAO.NAME)
public class SalaDAO extends DAO<Sala> {

    public static final String NAME = "salaDAO";
    private static final long serialVersionUID = 1L;
    
    @Override
    public List<Sala> findAll() {
    	return getNamedResultList(SalaQuery.LIST_SALA_ORDER_BY_NOME);
    }
    
    public List<Sala> findAllAtivo() {
    	return getNamedResultList(SalaQuery.LIST_SALA_ATIVO_ORDER_BY_NOME);
    }
    
    public List<Sala> listSalaByColegiada(UnidadeDecisoraColegiada colegiada) {
    	Map<String, Object> params = new HashMap<>(1);
    	params.put(SalaQuery.PARAM_COLEGIADA, colegiada);
    	return getNamedResultList(SalaQuery.LIST_SALA_FILTER_BY_COLEGIADA, params);
    }
    

}
