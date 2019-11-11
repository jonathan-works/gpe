package br.com.infox.epp.assinador;

import javax.inject.Inject;

import br.com.infox.epp.certificado.entity.CertificateSignature;

public class CertificateSignatureService {
	
	@Inject
	private CertificateSignatureSearch certificateSignatureSearch;

	public CertificateSignature findByTokenAndUUID(String token, String uuid) {
		return certificateSignatureSearch.findByTokenAndUUID(token, uuid);
	}

}
