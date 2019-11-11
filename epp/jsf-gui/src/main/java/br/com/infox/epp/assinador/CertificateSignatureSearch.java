package br.com.infox.epp.assinador;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;

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
	
	private TypedQuery<CertificateSignature> getQueryStatus(String token, CertificateSignatureGroupStatus status) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CertificateSignature> cq = cb.createQuery(CertificateSignature.class);
		Root<CertificateSignature> certificateSignature = cq.from(CertificateSignature.class);
		Path<CertificateSignatureGroup> certificateSignatureGroup = certificateSignature.join(CertificateSignature_.certificateSignatureGroup);
		
		Predicate tokenIgual = cb.equal(certificateSignatureGroup.get(CertificateSignatureGroup_.token), token);
		Predicate statusIgual = cb.equal(certificateSignature.get(CertificateSignature_.status), status);
		
		cq = cq.select(certificateSignature).where(tokenIgual, statusIgual);
		return getEntityManager().createQuery(cq);
	}
	
	public boolean possuiStatusDiferente(String token, CertificateSignatureGroupStatus status) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CertificateSignature> cq = cb.createQuery(CertificateSignature.class);
		Root<CertificateSignature> certificateSignature = cq.from(CertificateSignature.class);
		Path<CertificateSignatureGroup> certificateSignatureGroup = certificateSignature.join(CertificateSignature_.certificateSignatureGroup);
		
		Predicate tokenIgual = cb.equal(certificateSignatureGroup.get(CertificateSignatureGroup_.token), token);
		Predicate statusDiferente = cb.notEqual(certificateSignature.get(CertificateSignature_.status), status);
		
		cq = cq.select(certificateSignature).where(tokenIgual, statusDiferente);
		return getEntityManager().createQuery(cq).setMaxResults(1).getResultList().size() > 0;
	}
	
	public boolean possuiStatus(String token, CertificateSignatureGroupStatus status) {
		 return getQueryStatus(token, status).setMaxResults(1).getResultList().size() > 0;
	}
	
	public boolean possuiAguardando(String token) {
		 return possuiStatus(token, CertificateSignatureGroupStatus.W);
	}

	public List<CertificateSignature> findByStatus(String token, CertificateSignatureGroupStatus status) {
		return getQueryStatus(token, status).getResultList();
	}
}
