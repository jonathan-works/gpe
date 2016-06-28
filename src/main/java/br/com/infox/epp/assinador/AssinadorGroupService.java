package br.com.infox.epp.assinador;

import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import br.com.infox.epp.certificado.entity.TipoAssinatura;

public interface AssinadorGroupService {

	public enum StatusToken {
		ERRO, SUCESSO, DESCONHECIDO, AGUARDANDO_ASSINATURA, EXPIRADO
	}

	public static class DadosAssinatura {
		private UUID uuid;
		private StatusToken status;
		private String codigoErro;
		private String mensagemErro;
		private TipoAssinatura tipoAssinatura;
		private Integer idDocumentoBin;
		private byte[] certChain;
		private byte[] assinatura;

		public DadosAssinatura(UUID uuid, StatusToken statusToken, String codigoErro, String mensagemErro,
				TipoAssinatura tipoAssinatura, Integer idDocumentoBin, byte[] assinatura, byte[] certChain) {
			super();
			this.uuid = uuid;
			this.status = statusToken;
			this.codigoErro = codigoErro;
			this.mensagemErro = mensagemErro;
			this.tipoAssinatura = tipoAssinatura;
			this.idDocumentoBin = idDocumentoBin;
			this.certChain = certChain;
			this.assinatura = assinatura;
		}

		public String getCodigoErro() {
			return codigoErro;
		}

		public String getMensagemErro() {
			return mensagemErro;
		}

		public TipoAssinatura getTipoAssinatura() {
			return tipoAssinatura;
		}

		public Integer getIdDocumentoBin() {
			return idDocumentoBin;
		}

		protected String toBase64(byte[] data) {
			if (data == null) {
				return null; 
			}
			return Base64.encodeBase64String(data);
		}

		public byte[] getAssinatura() {
			return assinatura;
		}

		public String getAssinaturaBase64() {
			return toBase64(getAssinatura());
		}
		
		public UUID getUuid() {
			return uuid;
		}

		public byte[] getCertChain() {
			return certChain;
		}

		public String getCertChainBase64() {
			return toBase64(getCertChain());
		}

		public StatusToken getStatus() {
			return status;
		}

	}

	public String createNewGroupWithBinary(List<byte[]> documentos);

	public String createNewGroup(List<UUID> documentos);

	public void validarToken(String token);

	public boolean isTokenExpired(String token);

	public StatusToken getStatus(String token);

	public List<UUID> getAssinaveis(String token);

	public byte[] getSha256(String token, UUID uuidAssinavel);

	public void apagarGrupo(String token);

	public void cancelar(String token);

	public void erroProcessamento(String token, UUID uuidAssinavel, String codigoErro, String mensagem);

	public void atualizarAssinaturaTemporaria(String tokenGrupo, UUID uuidAssinavel,
			DadosAssinaturaLegada dadosAssinaturaLegada);

	public List<DadosAssinatura> getDadosAssinatura(String token);

	public DadosAssinatura getDadosAssinatura(String token, UUID uuidAssinavel);

}
