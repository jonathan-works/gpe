package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.ValidationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.seam.util.Base64;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.util.time.DateRange;

@Stateless
public class CertificateSignatureGroupService implements AssinadorGroupService, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CertificateSignatureGroupSearch certificateSignatureGroupSearch;
	@Inject
	private CertificateSignatureSearch certificateSignatureSearch;
	@Inject
	private CertificateSignatureService certificateSignatureService;
	@Inject
	private TokenAssinaturaService tokenAssinaturaService;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private DocumentoBinarioManager documentoBinarioManager;

	private static final int TOKEN_LIFESPAN = 8;

	protected EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public byte[] getBinario(DocumentoBin documentoBin) {
		return documentoBinarioManager.getData(documentoBin.getId());
	}
	
	private CertificateSignatureGroup createNewGroup() {
		String uuid = UUID.randomUUID().toString();
		CertificateSignatureGroup certificateSignatureGroup = new CertificateSignatureGroup();
		certificateSignatureGroup.setToken(uuid);
		certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.W);
		certificateSignatureGroup.setDataCriacao(new Date());
		
		getEntityManager().persist(certificateSignatureGroup);
		
		return certificateSignatureGroup;
	}
	
	private CertificateSignature newCertificateSignature(CertificateSignatureGroup certificateSignatureGroup, UUID uuid, byte[] data) {
		CertificateSignature certificateSignature = new CertificateSignature();
		certificateSignature.setCertificateSignatureGroup(certificateSignatureGroup);
		certificateSignature.setUuid(UUID.randomUUID().toString());
		certificateSignature.setStatus(certificateSignatureGroup.getStatus());
		certificateSignature.setSha256(DigestUtils.sha256(data));
		certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
		
		getEntityManager().persist(certificateSignature);
		
		return certificateSignature;
	}

	@Override
	public String createNewGroupWithBinary(List<byte[]> assinaveis) {
		CertificateSignatureGroup certificateSignatureGroup = createNewGroup();
		
		for (byte[] assinavel : assinaveis) {			
			newCertificateSignature(certificateSignatureGroup, UUID.randomUUID(), assinavel);
		}

		getEntityManager().flush();
		
		return certificateSignatureGroup.getToken();		
	}
	
	public String createNewGroup(List<UUID> documentos) {
		CertificateSignatureGroup certificateSignatureGroup = createNewGroup();

		for (UUID documento : documentos) {
			DocumentoBin documentoBin = documentoBinManager.getByUUID(documento);
			byte[] bin = getBinario(documentoBin);
			
			CertificateSignature certificateSignature = newCertificateSignature(certificateSignatureGroup, UUID.randomUUID(), bin);
			certificateSignature.setDocumentoBin(documentoBin);
			certificateSignature.setStatus(certificateSignatureGroup.getStatus());
		}

		getEntityManager().flush();

		return certificateSignatureGroup.getToken();
	}

	public void validarToken(String token) {
		CertificateSignatureGroup group = findByToken(token);
		if (group == null) {
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
	
	private StatusToken getStatusToken(CertificateSignatureGroupStatus status) {
		switch (status) {
		case X:
			return StatusToken.EXPIRADO;
		case S:
			return StatusToken.SUCESSO;
		case E:
			return StatusToken.ERRO;
		case W:
			return StatusToken.AGUARDANDO_ASSINATURA;
		default:
			return StatusToken.DESCONHECIDO;
		}		
	}
	
	public StatusToken getStatus(String token) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		return getStatusToken(certificateSignatureGroup.getStatus());
	}

	/**
	 * Retorna uma lista contendo os UUIDs dos documentos desse grupo
	 */
	public List<UUID> getAssinaveis(String token) {
		CertificateSignatureGroup group = findByToken(token);
		List<UUID> retorno = new ArrayList<>();
		for (CertificateSignature certificateSignature : group.getCertificateSignatureList()) {
			retorno.add(UUID.fromString(certificateSignature.getUuid()));
		}
		return retorno;
	}

	private CertificateSignatureGroup findByToken(String token) {
		CertificateSignatureGroup group = certificateSignatureGroupSearch.findByToken(token);

		if (group.getStatus() == CertificateSignatureGroupStatus.W && isTokenExpired(group)) {
			tokenAssinaturaService.expirarToken(token);
		}

		return group;
	}

	private void setStatus(String token, CertificateSignatureGroupStatus status) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		certificateSignatureGroup.setStatus(status);

		getEntityManager().persist(certificateSignatureGroup);
		getEntityManager().flush();
	}

	public void apagarGrupo(String token) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);

		for (CertificateSignature certificateSignature : certificateSignatureGroup.getCertificateSignatureList()) {
			getEntityManager().remove(certificateSignature);
		}

		getEntityManager().remove(certificateSignatureGroup);
		getEntityManager().flush();
	}

	public void cancelar(String token) {
		erroProcessamento(token, "Operação cancelada pelo assinador");
	}

	public void erroProcessamento(String token, String mensagem) {
		setStatus(token, CertificateSignatureGroupStatus.E);
	}

	private CertificateSignature findCertificateSignature(String tokenGrupo, UUID uuidAssinavel) {
		try {
			return certificateSignatureService.findByTokenAndUUID(tokenGrupo, uuidAssinavel.toString());
		} catch (NoResultException e) {
			throw new ValidationException("CertificateSignature não encontrado");
		}
	}

	private boolean isFinalizado(String token) {
		return !certificateSignatureSearch.possuiAguardando(token);
	}

	private boolean isSucesso(String token) {
		return !certificateSignatureSearch.possuiStatusDiferente(token, CertificateSignatureGroupStatus.S);
	}
	
	private void atualizarStatusGrupo(String token) {
		StatusToken statusAtual = getStatus(token);
		if (statusAtual != StatusToken.AGUARDANDO_ASSINATURA) {
			throw new RuntimeException("Não foi possível atualizar grupo com token " + token + ": Status diferente de " + StatusToken.AGUARDANDO_ASSINATURA);
		}
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		
		if(isSucesso(token)) {
			certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.S);			
		}
		else if(certificateSignatureSearch.possuiStatus(token, CertificateSignatureGroupStatus.E)) {
			certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.E);			
		}
		else {
			certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.U);						
		}
		
		getEntityManager().flush();
	}

	private void atualizarCertificateSignature(CertificateSignature certificateSignature) {
		getEntityManager().flush();

		String token = certificateSignature.getCertificateSignatureGroup().getToken();
		if (isFinalizado(token)) {
			atualizarStatusGrupo(token);
		}
	}
	
	@Override
	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuidAssinavel,
			DadosAssinaturaLegada dadosAssinaturaLegada) {
		CertificateSignature certificateSignature = findCertificateSignature(tokenGrupo, uuidAssinavel);
		if (certificateSignature.getStatus() != CertificateSignatureGroupStatus.W) {
			throw new ValidationException("Assinatura já processada com status " + certificateSignature.getStatus());
		}

		certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
		certificateSignature.setCertificateChain(dadosAssinaturaLegada.getCertChainBase64());
		certificateSignature.setSignature(dadosAssinaturaLegada.getSignature());
		certificateSignature.setStatus(CertificateSignatureGroupStatus.S);

		atualizarCertificateSignature(certificateSignature);
	}

	@Override
	public byte[] getSha256(String token, UUID uuidAssinavel) {
		CertificateSignature certificateSignature = findCertificateSignature(token, uuidAssinavel);
		return certificateSignature.getSha256();
	}

	@Override
	public void erroProcessamento(String token, UUID uuidAssinavel, String codigoErro, String mensagem) {
		CertificateSignature certificateSignature = findCertificateSignature(token, uuidAssinavel);
		certificateSignature.setStatus(CertificateSignatureGroupStatus.E);
		certificateSignature.setCodigoErro(codigoErro);
		certificateSignature.setMensagemErro(mensagem);
		atualizarCertificateSignature(certificateSignature);		
	}
	
	private DadosAssinatura toDadosAssinatura(CertificateSignature cs) {
		UUID uuid = UUID.fromString(cs.getUuid());
		StatusToken status = getStatusToken(cs.getStatus());
		Integer idDocumentoBin = cs.getDocumentoBin() == null ? null : cs.getDocumentoBin().getId();
		byte[] signature = cs.getSignature() == null ? null : Base64.decode(cs.getSignature());
		byte[] certChain = cs.getCertificateChain() == null ? null : Base64.decode(cs.getCertificateChain());
		
		return new DadosAssinatura(uuid, status, cs.getCodigoErro(), cs.getMensagemErro(), cs.getSignatureType(), idDocumentoBin, signature, certChain);
	}

	@Override
	public List<DadosAssinatura> getDadosAssinatura(String token) {
		CertificateSignatureGroup group = findByToken(token);
		List<DadosAssinatura> retorno = new ArrayList<>();
		for (CertificateSignature certificateSignature : group.getCertificateSignatureList()) {
			retorno.add(toDadosAssinatura(certificateSignature));
		}
		return retorno;
	}

	@Override
	public DadosAssinatura getDadosAssinatura(String token, UUID uuidAssinavel) {		
		CertificateSignature certificateSignature = findCertificateSignature(token, uuidAssinavel);
		return toDadosAssinatura(certificateSignature);
	}

}
