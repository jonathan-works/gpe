package br.com.infox.epp.assinador.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.CertificateSignatureGroupService;
import br.com.infox.epp.assinador.CertificateSignatureService;
import br.com.infox.epp.assinador.DadosAssinaturaLegada;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.TipoAssinatura;

@Stateless
public class DocumentoRestService {
	
	@Inject
	private CertificateSignatureAdapter certificateSignatureAdapter;
	@Inject
	private CertificateSignatureGroupService groupService;
	@Inject
	private CertificateSignatureService certificateSignatureService;
	@Inject
	AssinadorService assinadorService;
	
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	private List<Documento> toDocumentoList(List<CertificateSignature> signatures) {
		List<Documento> retorno = new ArrayList<>();
		for(CertificateSignature signature : signatures) {
			Documento documento = certificateSignatureAdapter.convert(signature);
			retorno.add(documento);
		}
		return retorno;		
	}
	
	List<Documento> listarAssinaturas(String token) {
		CertificateSignatureGroup group = groupService.findByToken(token);
		List<CertificateSignature> signatures = group.getCertificateSignatureList();
		return toDocumentoList(signatures);
		
	}
	
	private CertificateSignature getOrCreateAssinatura(Documento documento, String tokenGrupo) {
		CertificateSignature certificateSignature = null;
		try {
			certificateSignature = certificateSignatureService.findByTokenAndUUID(tokenGrupo, documento.getUuid().toString());			
		}
		catch(NoResultException e) {
			certificateSignature = new CertificateSignature();
			certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
			certificateSignature.setUuid(documento.getUuid().toString());
			CertificateSignatureGroup group = groupService.findByToken(tokenGrupo);
			certificateSignature.setCertificateSignatureGroup(group);
			getEntityManager().persist(certificateSignature);
		}
		
		Assinatura assinatura = documento.getAssinatura();
		byte[] signature = assinatura.getAssinatura();
		DadosAssinaturaLegada dadosAssinaturaLegada = assinadorService.getDadosAssinaturaLegada(signature);

		certificateSignature.setCertificateChain(dadosAssinaturaLegada.getCertChain());
		certificateSignature.setSignature(dadosAssinaturaLegada.getSignature());
		
		
		return certificateSignature;		
	}
	
	public void criarOuAtualizarAssinatura(Documento documento, String tokenGrupo) {
		getOrCreateAssinatura(documento, tokenGrupo);
		getEntityManager().flush();
	}
	
	public void criarOuAtualizarAssinaturasLote(List<Documento> documentos, String tokenGrupo) {
		for(Documento documento : documentos) {
			getOrCreateAssinatura(documento, tokenGrupo);
		}
		getEntityManager().flush();
	}

}
