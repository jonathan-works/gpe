package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.assinador.api.AssinaturaRest;
import br.com.infox.epp.assinador.api.DocumentoRest;
import br.com.infox.epp.assinador.api.TokenAssinaturaResource;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.rest.RestException;

public class TokenAssinaturaResourceImpl implements TokenAssinaturaResource {

	private String token;
	@Inject
	private CertificateSignatureGroupService groupService;
	
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void cancelar() {
		groupService.cancelar(token);
	}

	@Override
	public void erroProcessamento(RestException erro) {
		groupService.erroProcessamento(token, erro.getMessage());
	}

	@Override
	public DocumentoRest getDocumentoRest() {
		groupService.validarToken(token);
		DocumentoRestImpl documentoRestImpl = BeanManager.INSTANCE.getReference(DocumentoRestImpl.class);
		documentoRestImpl.setTokenGrupo(token);
		return documentoRestImpl;
	}

	@Override
	public void processamentoFinalizado() {
		groupService.processamentoFinalizado(token);
	}

	@Override
	public AssinaturaRest getAssinaturaRest() {
		groupService.validarToken(token);
		AssinaturaRestImpl assinaturaRestImpl = BeanManager.INSTANCE.getReference(AssinaturaRestImpl.class);
		assinaturaRestImpl.setTokenGrupo(token);
		return assinaturaRestImpl;
	}
}
