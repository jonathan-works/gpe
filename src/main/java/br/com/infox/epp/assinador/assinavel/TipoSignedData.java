package br.com.infox.epp.assinador.assinavel;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

public enum TipoSignedData {
	
	SHA256("SHA-256", NISTObjectIdentifiers.id_sha256.getId());
	
	private final String hashId;
	private final String oid;
	
	TipoSignedData(String id, String oid) {
		this.hashId = id;
		this.oid = oid;
	}

	public String getId() {
		return hashId;
	}
	
	public String getOid() {
		return oid;
	}

	public byte[] dataToSign(byte[] originalData) {
		if(hashId == null) {
			return originalData;
		}
		return DigestUtils.getDigest(hashId).digest(originalData);
	}
}
