package br.com.infox.epp.assinador;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup_;

public class CertificateSignatureGroupSearch {

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public CertificateSignatureGroup findByToken(String token) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CertificateSignatureGroup> cq = cb.createQuery(CertificateSignatureGroup.class);
		Root<CertificateSignatureGroup> certificateSignatureGroup = cq.from(CertificateSignatureGroup.class);
		
		Predicate tokenIgual = cb.equal(certificateSignatureGroup.get(CertificateSignatureGroup_.token), token);
		
		cq = cq.select(certificateSignatureGroup).where(tokenIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	
}
