package br.com.infox.epp.assinador.assinavel;

import org.apache.commons.codec.digest.DigestUtils;

public enum TipoSignedData {
	
	SHA256("SHA-256");
	
	private final String hashId;
	
	TipoSignedData(String id) {
		this.hashId = id;
	}

	public String getId() {
		return hashId;
	}
	
	public byte[] dataToSign(byte[] originalData) {
		if(hashId == null) {
			return originalData;
		}
		return DigestUtils.getDigest(hashId).digest(originalData);
	}
}
