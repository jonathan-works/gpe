package br.com.infox.epp.assinador.rest;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.api.Assinatura;
import br.com.infox.epp.assinador.api.AssinaturaResource;

public class AssinaturaResourceImpl implements AssinaturaResource {
	
	private String tokenGrupo;
	private UUID uuidAssinavel;
	
	@Inject
	private AssinadorService assinadorService;
	
	public AssinaturaResourceImpl() {
		
	}

	@Override
	public Response assinar(Assinatura assinatura) {
		assinadorService.assinarAssinavel(tokenGrupo, uuidAssinavel, assinatura.getAssinatura());
		return Response.noContent().build();
	}

	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}

	public void setUuidAssinavel(UUID uuidAssinavel) {
		this.uuidAssinavel = uuidAssinavel;
	}
}
