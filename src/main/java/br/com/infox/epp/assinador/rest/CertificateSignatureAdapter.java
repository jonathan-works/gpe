package br.com.infox.epp.assinador.rest;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import br.com.infox.epp.certificado.entity.CertificateSignature;

public class CertificateSignatureAdapter {
	
	public Documento convert(CertificateSignature certificateSignature) {
		byte[] assinatura = null;
		
		String base64 = certificateSignature.getSignature();
		if(base64 != null) {
			assinatura = Base64.decodeBase64(base64);
		}
		
		String strUUID = certificateSignature.getUuid();
		UUID uuid = UUID.fromString(strUUID);
		
		return new Documento(uuid, assinatura);
	}
}
