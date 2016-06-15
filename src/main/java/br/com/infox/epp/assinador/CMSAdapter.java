package br.com.infox.epp.assinador;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;

public class CMSAdapter {

    private static final String X509_CERTIFICATE_TYPE = "X.509";
    //private static final String CERT_CHAIN_ENCODING = "PkiPath";
    //private static final String DIGITAL_SIGNATURE_ALGORITHM_NAME = "SHA1withRSA";
    //private static final String CERT_CHAIN_VALIDATION_ALGORITHM = "PKIX";
    
	public DadosAssinaturaLegada convert(byte[] signature) {
		String base64 = Base64.encodeBase64String(signature);
		throw new UnsupportedOperationException();
	}
	
}
