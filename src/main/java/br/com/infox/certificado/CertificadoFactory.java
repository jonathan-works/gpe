package br.com.infox.certificado;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public final class CertificadoFactory {

    public static Certificado createCertificado(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        X509Certificate mainCertificate = certChain[0];
        Principal subjectDN = mainCertificate.getSubjectDN();
        String[] dados = subjectDN.getName().split(", ");
        for (String dado : dados) {
            String[] linha = dado.split("=");
            if (linha[0].equals("OU")) {
                String valor = linha[1].trim();
                if (valor.equals("Cert-JUS Poder Publico - A3")) {
                    return new CertJUSPoderPublico(certChain, privateKey);
                } else if (valor.equals("RFB e-CPF A3")) {
                    return new CertificadoECPF(certChain, privateKey);
                }
            }
        }
        return null;
    }
    
    public static Certificado createCertificado(X509Certificate[] certChain) throws CertificadoException {
        return createCertificado(certChain, null);
    }
    
    public static Certificado createCertificado(String certChainBase64) throws CertificadoException {
        return createCertificado(DigitalSignatureUtils.loadCertFromBase64String(certChainBase64));
    }
}