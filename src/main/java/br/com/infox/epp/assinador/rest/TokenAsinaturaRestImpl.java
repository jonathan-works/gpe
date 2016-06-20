package br.com.infox.epp.assinador.rest;

import br.com.infox.epp.assinador.api.TokenAssinaturaResource;
import br.com.infox.epp.assinador.api.TokenAssinaturaRest;
import br.com.infox.epp.cdi.config.BeanManager;

public class TokenAsinaturaRestImpl implements TokenAssinaturaRest {

	@Override
	public TokenAssinaturaResource getTokenAssinaturaResource(String token) {		
		TokenAssinaturaResourceImpl retorno = BeanManager.INSTANCE.getReference(TokenAssinaturaResourceImpl.class);
		retorno.setToken(token);
		return retorno;
	}

}
