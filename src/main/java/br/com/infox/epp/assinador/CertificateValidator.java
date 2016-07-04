package br.com.infox.epp.assinador;

import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateValidator {

	public boolean validarCertificado(List<X509Certificate> certChain) throws CertPathValidatorException;
 
}
