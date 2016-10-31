package br.com.infox.epp.system.custom.variables;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CustomVariableSearch extends PersistenceController {

	public CustomVariable getById(Long id) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CustomVariable> cq = cb.createQuery(CustomVariable.class);
		Root<CustomVariable> from = cq.from(CustomVariable.class);
		cq.where(cb.equal(from.get(CustomVariable_.id), id));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Object getCustomVariableByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CustomVariable> cq = cb.createQuery(CustomVariable.class);
		Root<CustomVariable> from = cq.from(CustomVariable.class);
		cq.where(cb.equal(from.get(CustomVariable_.codigo), codigo));
		try {
			return getEntityManager().createQuery(cq).getSingleResult().getTypedValue();
		} catch (NoResultException e) {
			return null;
		}
	}
}
