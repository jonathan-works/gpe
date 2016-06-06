package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.CategoriaQuery.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;

@Stateless
@AutoCreate
@Name(CategoriaDAO.NAME)
public class CategoriaDAO extends DAO<Categoria> {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "categoriaDAO";

    public List<Object[]> listProcessoByCategoria() {
        return getNamedResultList(LIST_PROCESSO_EPP_BY_CATEGORIA);
    }
    
    public List<Categoria> getCategoriasFromNatureza(Natureza natureza){
    	Map<String, Object> params = new HashMap<>();
		params.put(QUERY_PARAM_NATUREZA, natureza);
    	return getNamedResultList(LIST_CATEGORIAS_BY_NATUREZA, params);
    }
    
    @Override
    public List<Categoria> findAll() {
    	String hql = "select o from Categoria o order by o.categoria";
    	return getEntityManager().createQuery(hql, Categoria.class).getResultList();
    }

}
