package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.api.AssinavelResource;
import br.com.infox.epp.assinador.api.TokenAssinaturaResource;
import br.com.infox.epp.cdi.config.BeanManager;

public class TokenAssinaturaResourceImpl implements TokenAssinaturaResource {

	private String token;
	@Inject
	private AssinadorGroupService groupService;
	
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void processamentoCancelado() {
		groupService.cancelar(token);
	}

	@Override
	public String getStatus() {
		return groupService.getStatus(token).toString();
	}

	@Override
	public AssinavelResource getAssinavelResource() {
		groupService.validarToken(token);
		AssinavelResourceImpl assinavelResourceImpl = BeanManager.INSTANCE.getReference(AssinavelResourceImpl.class);
		assinavelResourceImpl.setTokenGrupo(token);
		return assinavelResourceImpl;
	}
}
