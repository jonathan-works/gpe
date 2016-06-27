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

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.certificado.entity.CertificateSignature;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.certificado.entity.TipoDados;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.util.time.DateRange;

@Stateless
public class CertificateSignatureGroupService implements AssinadorGroupService, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CertificateSignatureGroupSearch certificateSignatureGroupSearch;
	@Inject
	private CertificateSignatureService certificateSignatureService;
	@Inject
	private TokenAssinaturaService tokenAssinaturaService;

	private static final int TOKEN_LIFESPAN = 8;

	protected EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public String createNewGroup(List<UUID> documentos) {
		String uuid = UUID.randomUUID().toString();
		CertificateSignatureGroup certificateSignatureGroup = new CertificateSignatureGroup();
		certificateSignatureGroup.setToken(uuid);
		certificateSignatureGroup.setStatus(CertificateSignatureGroupStatus.W);
		certificateSignatureGroup.setDataCriacao(new Date());

		getEntityManager().persist(certificateSignatureGroup);

		for (UUID documento : documentos) {
			CertificateSignature certificateSignature = new CertificateSignature();
			certificateSignature.setCertificateSignatureGroup(certificateSignatureGroup);
			certificateSignature.setUuid(documento.toString());
			certificateSignature.setDataType(TipoDados.DOCUMENTO_BIN);
			getEntityManager().persist(certificateSignature);
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

	public StatusToken getStatus(String token) {
		CertificateSignatureGroup certificateSignatureGroup = findByToken(token);
		switch (certificateSignatureGroup.getStatus()) {
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

	/**
	 * Retorna uma lista contendo os UUIDs dos documentos desse grupo
	 */
	public List<UUID> getDocumentos(String token) {
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
		// Grupo não deve ser apagado nesse momento para manter compatibilidade
		// com código antigo
		// apagarGrupo(token);
	}

	public void processamentoFinalizado(String token) {
		setStatus(token, CertificateSignatureGroupStatus.S);
		// apagarGrupo(token);
	}

	@Override
	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuid,
			DadosAssinaturaLegada dadosAssinaturaLegada) {
		CertificateSignature certificateSignature = null;
		try {
			certificateSignature = certificateSignatureService.findByTokenAndUUID(tokenGrupo, uuid.toString());			
		}
		catch(NoResultException e) {
			throw new RuntimeException("CertificateSignature não encontrado");
		}
		
		certificateSignature.setSignatureType(TipoAssinatura.PKCS7);
		certificateSignature.setCertificateChain(dadosAssinaturaLegada.getCertChainBase64());
		certificateSignature.setSignature(dadosAssinaturaLegada.getSignature());
	}

}
