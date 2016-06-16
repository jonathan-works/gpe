package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.cdi.config.BeanManager;

public class TokenAsinaturaRestImpl implements TokenAssinaturaRest {

	@Inject
	private CertificateSignatureGroupService certificateSignatureGroupService;
	
	@Override
	public TokenAssinaturaResource getTokenAssinaturaResource(String token) {
		certificateSignatureGroupService.validarToken(token);
		
		TokenAssinaturaResourceImpl retorno = BeanManager.INSTANCE.getReference(TokenAssinaturaResourceImpl.class);
		retorno.setToken(token);
		return retorno;
		
	}

}
