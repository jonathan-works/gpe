package br.com.infox.epp.fluxo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPasta_;

@Stateless
@AutoCreate
@Name(ModeloPastaDAO.NAME)
public class ModeloPastaDAO extends DAO<ModeloPasta>{
	
    private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloPastaDao";

	public List<ModeloPasta> getByFluxo(Fluxo fluxo) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<ModeloPasta> cq = cb.createQuery(ModeloPasta.class);
	    Root<ModeloPasta> mp = cq.from(ModeloPasta.class);
	    cq.select(mp);
	    cq.where(cb.equal(mp.get(ModeloPasta_.fluxo), fluxo));
	    cq.orderBy(cb.asc(mp.get(ModeloPasta_.ordem)));
	    return getEntityManager().createQuery(cq).getResultList();
	}

    public List<ModeloPasta> getByIdFluxo(Integer idFluxo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloPasta> cq = cb.createQuery(ModeloPasta.class);
        Root<ModeloPasta> mp = cq.from(ModeloPasta.class);
        Join<ModeloPasta, Fluxo> fluxo = mp.join(ModeloPasta_.fluxo, JoinType.INNER);
        cq.select(mp);
        cq.where(cb.equal(fluxo.get(Fluxo_.idFluxo), idFluxo));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
