package br.com.infox.epp.assinador;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup_;
import br.com.infox.epp.certificado.entity.CertificateSignature_;

public class CertificateSignatureSearch {

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public CertificateSignature findByTokenAndUUID(String token, String uuid) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CertificateSignature> cq = cb.createQuery(CertificateSignature.class);
		Root<CertificateSignature> certificateSignature = cq.from(CertificateSignature.class);
		Path<CertificateSignatureGroup> certificateSignatureGroup = certificateSignature.join(CertificateSignature_.certificateSignatureGroup);
		
		Predicate tokenIgual = cb.equal(certificateSignatureGroup.get(CertificateSignatureGroup_.token), token);
		Predicate uuidIgual = cb.equal(certificateSignature.get(CertificateSignature_.uuid), uuid);
		
		cq = cq.select(certificateSignature).where(tokenIgual, uuidIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}

}
