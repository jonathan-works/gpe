package br.com.infox.epp.assinador;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.cdi.util.Beans;

public class CertificateValidatorPadrao implements CertificateValidator {

	@Inject
	private CertPathValidator certPathValidator;
	@Inject
	private CertificateFactory certificateFactory;
	
	/**
	 * É necessário habilitar esse property para que seja habilitado o DistributionPointFetcher utilizado para checar CRLs
	 */
	static {
		System.setProperty("com.sun.security.enableCRLDP", "true");		
	}
	
	@Override
	public boolean validarCertificado(List<X509Certificate> certChain) throws CertPathValidatorException {
		PKIXParameters params = Beans.getReference(PKIXParameters.class);
		CertPath certPath;
		try {
			certPath = certificateFactory.generateCertPath(certChain);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
		
		PKIXCertPathValidatorResult resultado;
		try {
			resultado = (PKIXCertPathValidatorResult)certPathValidator.validate(certPath, params);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		return resultado != null;
	}

}
