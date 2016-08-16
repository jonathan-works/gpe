package br.com.infox.epp.fluxo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.fluxo.query.NaturezaQuery;

@Stateless
@AutoCreate
@Name(NaturezaDAO.NAME)
public class NaturezaDAO extends DAO<Natureza> {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "naturezaDAO";
    
    @Override
    public List<Natureza> findAll() {
    	String hql = "select o from Natureza o order by o.natureza";
    	return getEntityManager().createQuery(hql, Natureza.class).getResultList();
    }

	public List<Natureza> findNaturezasPrimarias() {
		return getNamedResultList(NaturezaQuery.NATUREZA_FIND_BY_PRIMARIA);
	}
	
	public Natureza getByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Natureza> cq = cb.createQuery(Natureza.class);
		Root<Natureza> natureza = cq.from(Natureza.class);
		
		cq.select(natureza).where(
				cb.equal(natureza.get(Natureza_.codigo), codigo)
		);
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}

}
