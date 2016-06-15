package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.rest.RestException;

public class TokenAssinaturaResourceImpl implements TokenAssinaturaResource {

	private CertificateSignatureGroup group;
	@Inject
	private CertificateSignatureGroupService groupService;
	
	
	public void setGroup(CertificateSignatureGroup group) {
		this.group = group;
	}

	@Override
	public void cancelar() {
		groupService.cancelar(group.getToken());
	}

	@Override
	public void erroProcessamento(RestException erro) {
		groupService.erroProcessamento(group.getToken(), erro.getMessage());
	}

	@Override
	public DocumentoRest getDocumentoRest() {
		DocumentoRestImpl documentoRestImpl = BeanManager.INSTANCE.getReference(DocumentoRestImpl.class);
		documentoRestImpl.setGroup(group);
		return documentoRestImpl;
	}

	
}
