package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_PROCESSO_EPP_BY_CATEGORIA;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;

@Name(CategoriaDAO.NAME)
@AutoCreate
public class CategoriaDAO extends DAO<Categoria> {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "categoriaDAO";

    public List<Object[]> listProcessoByCategoria() {
        return getNamedResultList(LIST_PROCESSO_EPP_BY_CATEGORIA);
    }
    
    @Override
    public List<Categoria> findAll() {
    	String hql = "select o from Categoria o order by o.categoria";
    	return getEntityManager().createQuery(hql, Categoria.class).getResultList();
    }

}
