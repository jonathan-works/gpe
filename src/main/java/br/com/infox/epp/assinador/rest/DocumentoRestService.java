package br.com.infox.epp.assinador.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.CertificateSignatureService;
import br.com.infox.epp.assinador.DadosAssinaturaLegada;
import br.com.infox.epp.assinador.api.Assinatura;
import br.com.infox.epp.assinador.api.Documento;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.TipoAssinatura;

@Stateless
public class DocumentoRestService {
	
	@Inject
	private AssinadorGroupService groupService;
	@Inject
	private CertificateSignatureService certificateSignatureService;
	@Inject
	AssinadorService assinadorService;
	
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<Documento> listarDocumentos(String token) {
		List<UUID> documentos = groupService.getDocumentos(token);
		List<Documento> retorno = new ArrayList<>();
		for(UUID uuid : documentos) {
			Documento documento = new Documento(uuid);
			retorno.add(documento);
		}
		return retorno;
	}
	
	private CertificateSignature updateCertificateSignature(String tokenGrupo, UUID uuid, byte[] signature) {
		CertificateSignature certificateSignature = null;
		try {
			certificateSignature = certificateSignatureService.findByTokenAndUUID(tokenGrupo, uuid.toString());			
		}
		catch(NoResultException e) {
			throw new RuntimeException("CertificateSignature não encontrado");
		}
		
		DadosAssinaturaLegada dadosAssinaturaLegada = assinadorService.getDadosAssinaturaLegada(signature);

		certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
		certificateSignature.setCertificateChain(dadosAssinaturaLegada.getCertChainBase64());
		certificateSignature.setSignature(dadosAssinaturaLegada.getSignature());
		
		return certificateSignature;		
	}
	
	private void assinarDocumentoSemFlush(Assinatura assinatura, String tokenGrupo) {
		if(tokenGrupo != null) {
			updateCertificateSignature(tokenGrupo, assinatura.getUuidDocumento(), assinatura.getAssinatura());
		}
		assinadorService.assinarDocumento(assinatura.getUuidDocumento(), assinatura.getCodigoPerfil(), assinatura.getCodigoLocalizacao(), assinatura.getAssinatura());
	}
	
	public void assinarDocumento(Assinatura assinatura, String tokenGrupo) {
		assinarDocumentoSemFlush(assinatura, tokenGrupo);
		getEntityManager().flush();
	}
	
	public void assinarDocumentos(List<Assinatura> assinaturas, String tokenGrupo) {
		for(Assinatura assinatura : assinaturas) {
			assinarDocumentoSemFlush(assinatura, tokenGrupo);
		}
		getEntityManager().flush();
	}
}
