package br.com.infox.epp.fluxo.dao;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Natureza;
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

}
