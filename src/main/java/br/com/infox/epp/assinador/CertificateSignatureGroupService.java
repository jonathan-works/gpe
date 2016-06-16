package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.util.time.DateRange;

@Stateless
public class CertificateSignatureGroupService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CertificateSignatureGroupSearch certificateSignatureGroupSearch;

	private static final int TOKEN_LIFESPAN = 8;

	protected EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public CertificateSignatureGroup createNewGroup(List<DocumentoBin> documentos) {
		String uuid = UUID.randomUUID().toString();
		CertificateSignatureGroup certificateSignatureGroup = new CertificateSignatureGroup();
		certificateSignatureGroup.setToken(uuid);
		certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.W);
		certificateSignatureGroup.setDataCriacao(new Date());

		getEntityManager().persist(certificateSignatureGroup);

		for (DocumentoBin documentoBin : documentos) {
			CertificateSignature certificateSignature = new CertificateSignature();
			certificateSignature.setCertificateSignatureGroup(certificateSignatureGroup);
			certificateSignature.setUuid(documentoBin.getUuid().toString());
			getEntityManager().persist(certificateSignature);
		}

		getEntityManager().flush();

		return certificateSignatureGroup;
	}

	public void validarToken(String token) {
		CertificateSignatureGroup group = findByToken(token);
		if(group == null) {
			throw new ValidationException("Token inválido");
		}
		
		CertificateSignatureGroupStatus status = group.getStatus();

		// Status válido
		if (status == CertificateSignatureGroupStatus.W) {
			return;
		}

		switch (group.getStatus()) {
		case X:
			throw new ValidationException("Token expirado");
		case S:
		case E:
			throw new ValidationException("Token já processado");
		default:
			throw new ValidationException("Token com status desconhecido");
		}

	}

	private boolean isTokenExpired(CertificateSignatureGroup group) {
		return new DateRange(group.getDataCriacao(), new Date()).get(DateRange.MINUTES) > TOKEN_LIFESPAN;
	}

	public boolean isTokenExpired(String token) {
		return isTokenExpired(findByToken(token));
	}

	private void expirarToken(CertificateSignatureGroup group) {
		group.setStatus(CertificateSignatureGroupStatus.X);
		getEntityManager().flush();
	}

	public void expirarToken(String token) {
		expirarToken(findByToken(token));

	}

	public CertificateSignatureGroup findByToken(String token) {
		CertificateSignatureGroup group = certificateSignatureGroupSearch.findByToken(token);

		if (group.getStatus() == CertificateSignatureGroupStatus.W && isTokenExpired(group)) {
			expirarToken(group);
		}

		return group;
	}

	public void cancelar(String token) {
		erroProcessamento(token, "Operação cancelada pelo assinador");
	}
	
	private void setStatus(String token, CertificateSignatureGroupStatus status) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		certificateSignatureGroup.setStatus(status);

		getEntityManager().persist(certificateSignatureGroup);
		getEntityManager().flush();		
	}

	public void erroProcessamento(String token, String mensagem) {
		setStatus(token, CertificateSignatureGroupStatus.E);
	}
	
	public void processamentoFinalizado(String token) {
		setStatus(token, CertificateSignatureGroupStatus.S);
	}

}
