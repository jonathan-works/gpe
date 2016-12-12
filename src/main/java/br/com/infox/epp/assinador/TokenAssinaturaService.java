package br.com.infox.epp.assinador;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;

@Stateless
public class TokenAssinaturaService {

	@Inject
	private CertificateSignatureGroupSearch certificateSignatureGroupSearch;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void expirarToken(String token) {
		CertificateSignatureGroup group = certificateSignatureGroupSearch.findByToken(token);
		group.setStatus(CertificateSignatureGroupStatus.X);
		certificateSignatureGroupSearch.getEntityManager().flush();
	}
	
	
}
