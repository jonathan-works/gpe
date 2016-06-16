package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class AssinaturaRestImpl implements AssinaturaRest {

	@Inject
	private DocumentoRestService documentoRestService;
	
	private String tokenGrupo;
	private DocumentoBin documentoBin;
	
	public void setDocuemntoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}
	
	public void setTokenGrupo(String tokenGrupo) {
		this.tokenGrupo = tokenGrupo;
	}

	@Override
	public Response novaAssinatura(Assinatura assinatura) {
		Documento documento = new Documento(documentoBin.getUuid(), assinatura);
		documentoRestService.criarOuAtualizarAssinatura(documento, tokenGrupo);
		return Response.noContent().build();
	}
	
}
