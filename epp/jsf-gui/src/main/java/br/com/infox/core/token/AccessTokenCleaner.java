package br.com.infox.core.token;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AccessTokenCleaner implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private AccessTokenManager accessTokenManager;
	
	@PostConstruct
	private void clearTokens() {
		accessTokenManager.clearTokens();
	}
}
