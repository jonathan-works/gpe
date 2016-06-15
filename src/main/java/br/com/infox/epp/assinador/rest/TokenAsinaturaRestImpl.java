package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;

public class TokenAsinaturaRestImpl implements TokenAssinaturaRest {

	@Inject
	private CertificateSignatureGroupService certificateSignatureGroupService;
	
	@Override
	public TokenAssinaturaResource getTokenAssinaturaResource(String token) {
		TokenAssinaturaResourceImpl retorno = BeanManager.INSTANCE.getReference(TokenAssinaturaResourceImpl.class);
		CertificateSignatureGroup group = certificateSignatureGroupService.findByToken(token);
		retorno.setGroup(group);
		return retorno;
		
	}

}
