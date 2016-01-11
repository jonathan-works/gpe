package br.com.infox.epp.painel.caixa;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;

@Stateless
@AutoCreate
@Name(CaixaDAO.NAME)
public class CaixaDAO extends DAO<Caixa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "caixaDAO";
    
    public Caixa getCaixaByDestinationNodeKeyNodeAnterior(String taskKey, Long idNodeAnterior) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Caixa> cq = cb.createQuery(Caixa.class);
        Root<Caixa> caixa = cq.from(Caixa.class);
        cq.where(
                cb.equal(caixa.get(Caixa_.taskKey), cb.literal(taskKey)),
                cb.equal(caixa.get(Caixa_.idNodeAnterior), cb.literal(idNodeAnterior))
        );
    	return getSingleResult(getEntityManager().createQuery(cq));
    }
    
    public List<Caixa> getCaixasByTaskKey(String taskKey) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Caixa> cq = cb.createQuery(Caixa.class);
        Root<Caixa> caixa = cq.from(Caixa.class);
        cq.where(
                cb.equal(caixa.get(Caixa_.taskKey), cb.literal(taskKey))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
