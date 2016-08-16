package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_CATEGORIAS_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_PROCESSO_EPP_BY_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaQuery.QUERY_PARAM_NATUREZA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;

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
    
	public Categoria getByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
		Root<Categoria> categoria = cq.from(Categoria.class);
		
		cq.select(categoria).where(
				cb.equal(categoria.get(Categoria_.codigo), codigo)
		);
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
    
    
    public List<Categoria> getCategoriasPrimariasAtivas() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
        Root<NaturezaCategoriaFluxo> ncf = cq.from(NaturezaCategoriaFluxo.class);
        Join<NaturezaCategoriaFluxo, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria);
        Path<Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);
        
        cq.select(categoria);
        cq.distinct(true);
        cq.where(
        		cb.equal(categoria.get(Categoria_.ativo), true),
        		cb.equal(natureza.get(Natureza_.primaria), true)
		);
        
        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
