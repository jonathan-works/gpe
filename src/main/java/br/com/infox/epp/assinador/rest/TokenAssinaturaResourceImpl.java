package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.api.AssinavelResource;
import br.com.infox.epp.assinador.api.TokenAssinaturaResource;
import br.com.infox.epp.cdi.config.BeanManager;

public class TokenAssinaturaResourceImpl implements TokenAssinaturaResource {

	private String token;
	@Inject
	private AssinadorService assinadorService;
	
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void processamentoCancelado() {
		assinadorService.cancelar(token);
	}

	@Override
	public String getStatus() {
		return assinadorService.getStatus(token).toString();
	}

	@Override
	public AssinavelResource getAssinavelResource() {
		assinadorService.validarNovoToken(token);
		AssinavelResourceImpl assinavelResourceImpl = BeanManager.INSTANCE.getReference(AssinavelResourceImpl.class);
		assinavelResourceImpl.setTokenGrupo(token);
		return assinavelResourceImpl;
	}
}
