package br.com.infox.epp.assinador.assinavel;

import org.apache.commons.codec.digest.DigestUtils;

public enum TipoHash {
	
	SHA256("SHA-256");
	
	private final String id;
	
	TipoHash(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public byte[] digest(byte[] data) {
		return DigestUtils.getDigest(id).digest(data);
	}
}
