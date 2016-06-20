package br.com.infox.epp.assinador.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import br.com.infox.epp.assinador.api.Assinatura;
import br.com.infox.epp.assinador.api.AssinaturaRest;

public class AssinaturaRestImpl implements AssinaturaRest {

	@Inject
	private DocumentoRestService documentoRestService;
	
	private String tokenGrupo;
	
	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}

	@Override
	public Response novaAssinatura(Assinatura assinatura) {
		documentoRestService.assinarDocumento(assinatura, tokenGrupo);
		return Response.noContent().build();
	}

	@Override
	public Response assinarDocumentosLote(List<Assinatura> assinaturas) {
		documentoRestService.assinarDocumentos(assinaturas, tokenGrupo);
		return Response.noContent().build();
	}
	
}
