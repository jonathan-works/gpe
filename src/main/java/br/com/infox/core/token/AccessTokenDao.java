package br.com.infox.core.token;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccessTokenDao extends Dao<AccessToken, Long> {
	public AccessTokenDao() {
		super(AccessToken.class);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isTokenValid(UUID token, TokenRequester tokenRequester) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
		Root<AccessToken> accessToken = query.from(AccessToken.class);
		query.where(cb.equal(accessToken.get(AccessToken_.token), token), cb.equal(accessToken.get(AccessToken_.tokenRequester), tokenRequester));
		query.select(cb.literal(1));
		return !entityManager.createQuery(query).setMaxResults(1).getResultList().isEmpty();
	}
}
