package br.com.infox.epp.assinador;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.util.time.DateRange;

@Stateless
public class CertificateSignatureGroupService {

	@Inject
	private CertificateSignatureGroupSearch certificateSignatureGroupSearch;
	
    private static final int TOKEN_LIFESPAN = 8;
    
    protected EntityManager getEntityManager() {
    	return EntityManagerProducer.getEntityManager();
    }
    
    public boolean isTokenExpired(String token) {
    	CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
        return new DateRange(certificateSignatureGroup.getDataCriacao(), new Date()).get(DateRange.MINUTES) > TOKEN_LIFESPAN;
    }
    
	public CertificateSignatureGroup findByToken(String token) {
		return certificateSignatureGroupSearch.findByToken(token);
	}
	
	public void cancelar(String token) {
		erroProcessamento(token, "Operação cancelada pelo assinador");
	}
	
	public void erroProcessamento(String token, String mensagem) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.E);
		
		getEntityManager().persist(certificateSignatureGroup);
		getEntityManager().flush();		
	}
}
