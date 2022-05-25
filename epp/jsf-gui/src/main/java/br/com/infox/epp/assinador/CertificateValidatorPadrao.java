package br.com.infox.epp.assinador;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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

public class CertificateValidatorPadrao implements CertificateValidator {

    private static final String CERTIFICATE_FACTORY_TYPE_NAME = "X.509";
    private static final String CERT_PATH_VALIDATOR_ALGORITHM_NAME = "PKIX";

    /**
     * É necessário habilitar esse property para que seja habilitado o DistributionPointFetcher utilizado para checar CRLs
     */
    static {
        System.setProperty("com.sun.security.enableCRLDP", "true");
    }

    @Inject
    private TrustStoreService trustStoreService;

    @Override
    public boolean validarCertificado(List<X509Certificate> certChain) throws CertPathValidatorException {
        CertPath certPath;
        try {
            certPath = createCertificateFactory().generateCertPath(certChain);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        PKIXCertPathValidatorResult resultado;
        try {
            resultado = (PKIXCertPathValidatorResult)createCertPathValidator().validate(certPath, createPKIXParameters());
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return resultado != null;
    }

    private CertPathValidator createCertPathValidator() throws NoSuchAlgorithmException {
        CertPathValidator certPathValidator = CertPathValidator.getInstance(CERT_PATH_VALIDATOR_ALGORITHM_NAME);
        return certPathValidator;
    }

    private PKIXParameters createPKIXParameters() throws KeyStoreException, InvalidAlgorithmParameterException {
        KeyStore keyStore = trustStoreService.getTrustStore();
        PKIXParameters params = new PKIXParameters(keyStore);
        params.setRevocationEnabled(false);
        return params;
    }
    private CertificateFactory createCertificateFactory() throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE_NAME);
        return certificateFactory;
    }

}
