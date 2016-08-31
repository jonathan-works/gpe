package br.com.infox.epp.assinador;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.certificado.entity.TipoAssinatura;

public class DadosAssinatura {
	private UUID uuid;
	private StatusToken status;
	private String codigoErro;
	private String mensagemErro;
	private TipoAssinatura tipoAssinatura;
	private UUID uuidDocumentoBin;
	private byte[] certChain;
	private byte[] assinatura;
	private byte[] signedData;
	private TipoSignedData tipoSignedData;

	public DadosAssinatura(UUID uuid, StatusToken statusToken, String codigoErro, String mensagemErro,
			TipoAssinatura tipoAssinatura, UUID uuidDocumentoBin, byte[] assinatura, byte[] certChain, byte[] signedData, TipoSignedData tipoSignedData) {
		super();
		this.uuid = uuid;
		this.status = statusToken;
		this.codigoErro = codigoErro;
		this.mensagemErro = mensagemErro;
		this.tipoAssinatura = tipoAssinatura;
		this.uuidDocumentoBin = uuidDocumentoBin;
		this.certChain = certChain;
		this.assinatura = assinatura;
		this.signedData = signedData;
		this.tipoSignedData = tipoSignedData;
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

	public UUID getUuidDocumentoBin() {
		return uuidDocumentoBin;
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

	public byte[] getSignedData() {
		return signedData;
	}

	public TipoSignedData getTipoSignedData() {
		return tipoSignedData;
	}

}