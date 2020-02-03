package br.com.infox.epp.assinador;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class AssinadorProducer {

	@Inject
	private TrustStoreService trustStoreService;

	@Produces
	public CertPathValidator createCertPath() throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException {
		CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");

		return certPathValidator;
	}

	@Produces
	public PKIXParameters createPKIXParameters() throws KeyStoreException, InvalidAlgorithmParameterException {
		KeyStore keyStore = trustStoreService.getTrustStore();
		PKIXParameters params = new PKIXParameters(keyStore);
		params.setRevocationEnabled(false); //TODO Verificar como gerar certificado com revogação
		return params;
	}

	@Produces
	public CertificateFactory createCertificateFactory() throws CertificateException {
		return CertificateFactory.getInstance("X.509");
	}
}
