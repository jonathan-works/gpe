package br.com.infox.core.token;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AccessTokenManager {

	@Inject
	private AccessTokenDao accessTokenDao;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void persist(AccessToken accessToken) {
		accessTokenDao.persist(accessToken);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove(AccessToken accessToken) {
		accessTokenDao.remove(accessToken);
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeAsynchronous(AccessToken accessToken) {
	    accessTokenDao.remove(accessToken);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void clearTokens() {
		accessTokenDao.getEntityManager().createQuery("delete from AccessToken").executeUpdate();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void clearTokens() {
		accessTokenDao.getEntityManager().createQuery("delete from AccessToken").executeUpdate();
	}
}
