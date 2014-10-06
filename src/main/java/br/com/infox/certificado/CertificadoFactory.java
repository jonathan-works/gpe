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
        int i = 0;
        for (String dado : dados) {
            String[] linha = dado.split("=");
            if (linha[0].equals("OU")) {
                i++;
                if (i == 3) { // O OU que possui a identificação do tipo do certificado é o 3º OU, contando do nível mais baixo para o mais alto nos OUs
                    String valor = linha[1].trim();
                    if (valor.startsWith("Cert-JUS Poder Publico")) {
                        return new CertJUSPoderPublico(certChain, privateKey);
                    } else if (valor.startsWith("Cert-JUS Institucional")) {
                        return new CertJUSInstitucional(certChain, privateKey);
                    } else if (valor.startsWith("RFB e-CPF")) {
                        return new CertificadoECPF(certChain, privateKey);
                    }
                    throw new CertificadoException("Tipo de certificado não reconhecido: " + valor);
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