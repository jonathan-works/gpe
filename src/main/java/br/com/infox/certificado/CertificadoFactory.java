package br.com.infox.certificado;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public final class CertificadoFactory {

    public static Certificado createCertificado(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
    	return new CertificadoGenerico(certChain, privateKey);
    }
    
    public static Certificado createCertificado(X509Certificate[] certChain) throws CertificadoException {
        return createCertificado(certChain, null);
    }
    
    public static Certificado createCertificado(String certChainBase64) throws CertificadoException {
        return createCertificado(DigitalSignatureUtils.loadCertFromBase64String(certChainBase64));
    }
}