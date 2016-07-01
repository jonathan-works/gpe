package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.ValidationException;

import org.jboss.seam.util.Base64;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinSource;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelSource;
import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
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
	private DocumentoBinAssinavelService documentoBinAssinavelService;

	private static final int TOKEN_LIFESPAN = 8;

	protected EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public byte[] getBinario(DocumentoBin documentoBin) {
		return documentoBinAssinavelService.getDadosAssinaveis(documentoBin.getId());
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
	
	private CertificateSignature newCertificateSignature(CertificateSignatureGroup certificateSignatureGroup, AssinavelSource assinavelSource) {
		CertificateSignature certificateSignature = new CertificateSignature();
		certificateSignature.setCertificateSignatureGroup(certificateSignatureGroup);
		certificateSignature.setUuid(UUID.randomUUID().toString());
		certificateSignature.setStatus(certificateSignatureGroup.getStatus());
		certificateSignature.setSha256(assinavelSource.dataToSign(TipoSignedData.SHA256));
		certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
		
		if(assinavelSource instanceof AssinavelDocumentoBinSource) {
			Integer idDocumentoBin = ((AssinavelDocumentoBinSource)assinavelSource).getIdDocumentoBin();
			DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
			certificateSignature.setDocumentoBin(documentoBin);
			//Deve ser o UUID documentoBin para funcionar com código legado
			certificateSignature.setUuid(documentoBin.getUuid().toString());
		}
		
		getEntityManager().persist(certificateSignature);
		
		return certificateSignature;
	}

	@Override
	public String createNewGroupWithAssinavelProvider(AssinavelProvider assinavelProvider) {
		CertificateSignatureGroup certificateSignatureGroup = createNewGroup();
		
		Iterator<AssinavelSource> it = assinavelProvider.getAssinaveis().iterator();
		while(it.hasNext()) {
			AssinavelSource source = it.next();
			newCertificateSignature(certificateSignatureGroup, source);
		}
		getEntityManager().flush();
		
		return certificateSignatureGroup.getToken();		
	}
	
	@Override
	public void validarToken(String token) {
		CertificateSignatureGroup group = findByToken(token);
		if (group == null) {
			throw new ValidationException("Token inválido");
		}

		CertificateSignatureGroupStatus status = group.getStatus();

		// Status válido
		if (status == CertificateSignatureGroupStatus.S) {
			return;
		}

		switch (group.getStatus()) {
		case X:
			throw new ValidationException("Token expirado");
		case W:
			throw new ValidationException("Token não assinado");
		case E:
			throw new ValidationException("Erro na assinatura do token");
		default:
			throw new ValidationException("Token com status desconhecido");
		}
	}
	
	@Override
	public void validarNovoToken(String token) {
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
		getEntityManager().refresh(certificateSignatureGroup);
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
	
	private void refresh(CertificateSignatureGroup group) {
		getEntityManager().refresh(group);
		List<CertificateSignature> certificateSignatures = group.getCertificateSignatureList();
		if(certificateSignatures != null) {
			for(CertificateSignature cs : certificateSignatures) {
				getEntityManager().refresh(cs);
			}
		}
	}

	private CertificateSignatureGroup findByToken(String token) {
		CertificateSignatureGroup group = certificateSignatureGroupSearch.findByToken(token);
		refresh(group);
		
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
		
		return new DadosAssinatura(uuid, status, cs.getCodigoErro(), cs.getMensagemErro(), cs.getSignatureType(), idDocumentoBin, signature, certChain, cs.getSha256(), TipoSignedData.SHA256);
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
