package br.com.infox.certificado.util;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jboss.seam.util.Base64;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

/**
 * Utility class for digital signatures and certificates verification.
 * 
 * Verification of digital signature aims to confirm or deny that given
 * signature is created by signing given document with the private key
 * corresponding to given certificate. Verification of signatures is done with
 * the standard digital signature verification algorithm, provided by Java
 * Cryptography API: 1. The message digest is calculated from given document. 2.
 * The original message digest is obtained by decrypting the signature with the
 * public key of the signer (this public key is taken from the signer's
 * certificate). 3. Values calculated in step 1. and step 2. are compared.
 * 
 * Verification of a certificate aims to check if the certificate is valid
 * wihtout inspecting its certification chain (sometimes it is unavailable). The
 * certificate verification is done in two steps: 1. The certificate validity
 * period is checked against current date. 2. The certificate is checked if it
 * is directly signed by some of the trusted certificates that we have. A list
 * of trusted certificates is supported for this direct certificate verification
 * process. If we want to successfully validate the certificates issued by some
 * certification authority (CA), we need to add the certificate of this CA in
 * our trusted list. Note that some CA have several certificates and we should
 * add only that of them, which the CA directly uses for issuing certificates to
 * its clients.
 * 
 * Verification of a certification chains aims to check if given certificate is
 * valid by analysing its certification chain. A certification chain always
 * starts with the user certificate that should be verified, then several
 * intermediate CA certificates follow and at the end of the chain stays some
 * root CA certificate. The verification process includes following steps
 * (according to PKIX algorithm): 1. Check the certificate validity period
 * against current date. 2. Check if each certificate in the chain is signed by
 * the previous. 3. Check if all the certificates in the chain, except the
 * first, belong to some CA, i.e. if they are authorized to be used for signing
 * other certificates. 4. Check if the root CA certificate in the end of the
 * chain is trusted, i.e. if is it in the list of trusted root CA certificates.
 * The verification process uses PKIX algorithm, defined in RFC-3280, but don't
 * use CRL lists.
 * 
 * This file is part of NakovDocumentSigner digital document signing framework
 * for Java-based Web applications: http://www.nakov.com/documents-signing/
 * 
 * Copyright (c) 2003 by Svetlin Nakov - http://www.nakov.com National Academy
 * for Software Development - http://academy.devbg.org All rights reserved. This
 * code is freeware. It can be used for any purpose as long as this copyright
 * statement is not removed or modified.
 */
public final class DigitalSignatureUtils {

    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private static final String CERT_CHAIN_ENCODING = "PkiPath";
    private static final String DIGITAL_SIGNATURE_ALGORITHM_NAME = "SHA1withRSA";
    private static final String CERT_CHAIN_VALIDATION_ALGORITHM = "PKIX";

    private static final LogProvider LOG = Logging.getLogProvider(DigitalSignatureUtils.class);

    private DigitalSignatureUtils() {
    }

    /**
     * Loads X.509 certificate from DER-encoded binary stream.
     */
    public static X509Certificate loadX509CertificateFromStream(
            InputStream aCertStream) throws GeneralSecurityException {
        CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        return (X509Certificate) cf.generateCertificate(aCertStream);
    }

    /**
     * Loads X.509 certificate from DER-encoded binary file (.CER file).
     */
    public static X509Certificate loadX509CertificateFromCERFile(
            String aFileName) throws GeneralSecurityException, IOException {
        FileInputStream fis = new FileInputStream(aFileName);
        X509Certificate cert = null;
        try {
            cert = loadX509CertificateFromStream(fis);
        } finally {
            fis.close();
        }
        return cert;
    }

    /**
     * Loads a certification chain from given Base64-encoded string, containing
     * ASN.1 DER formatted chain, stored with PkiPath encoding.
     */
    public static CertPath loadCertPathFromBase64String(
            String aCertChainBase64Encoded) throws CertificateException, IOException {
        byte[] certChainEncoded = Base64.decode(aCertChainBase64Encoded);
        CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        InputStream certChainStream = new ByteArrayInputStream(certChainEncoded);
        CertPath certPath;
        try {
            certPath = cf.generateCertPath(certChainStream, CERT_CHAIN_ENCODING);
        } finally {
            certChainStream.close();
        }
        return certPath;
    }

    public static CertPath loadCertPathString(byte[] aCertChainBase64Encoded) throws CertificateException, IOException {
        byte[] certChainEncoded = aCertChainBase64Encoded;
        CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        InputStream certChainStream = new ByteArrayInputStream(certChainEncoded);
        CertPath certPath;
        try {
            certPath = cf.generateCertPath(certChainStream, CERT_CHAIN_ENCODING);
        } finally {
            certChainStream.close();
        }
        return certPath;
    }

    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    public static X509Certificate[] loadCertFromBase64String(
            String certChainBase64Encoded) throws CertificadoException {
        CertPath mCertPath;
        try {
            mCertPath = DigitalSignatureUtils.loadCertPathFromBase64String(certChainBase64Encoded);
            List certsInChain = mCertPath.getCertificates();
            return (X509Certificate[]) certsInChain.toArray(new X509Certificate[certsInChain.size()]);
        } catch (Exception e) {
            throw new CertificadoException(e.getMessage(), e);
        }
    }

    /**
     * Verifies given digital singature. Checks if given signature is obtained
     * by signing given document with the private key, corresponing to given
     * public key.
     */
    public static boolean verifyDocumentSignature(byte[] aDocument,
            PublicKey aPublicKey, byte[] aSignature) throws GeneralSecurityException {
        Signature signatureAlgorithm = Signature.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
        signatureAlgorithm.initVerify(aPublicKey);
        signatureAlgorithm.update(aDocument);
        return signatureAlgorithm.verify(aSignature);
    }

    /**
     * Verifies given digital singature. Checks if given signature is obtained
     * by signing given document with the private key, corresponing to given
     * certificate.
     */
    public static boolean verifyDocumentSignature(byte[] aDocument,
            X509Certificate aCertificate, byte[] aSignature) throws GeneralSecurityException {
        PublicKey publicKey = aCertificate.getPublicKey();
        return verifyDocumentSignature(aDocument, publicKey, aSignature);
    }

    /**
     * Verifies a certificate. Checks its validity period and tries to find a
     * trusted certificate from given list of trusted certificates that is
     * directly signed given certificate. The certificate is valid if no
     * exception is thrown.
     * 
     * @param aCertificate the certificate to be verified.
     * @param aTrustedCertificates a list of trusted certificates to be used in
     *        the verification process.
     * 
     * @throws GeneralSecurityException
     */
    public static void verifyCertificate(X509Certificate aCertificate,
            X509Certificate[] aTrustedCertificates) throws GeneralSecurityException {
        // First check certificate validity period
        aCertificate.checkValidity();

        // Check if the certificate is signed by some of the given trusted
        // certificates
        for (int i = 0; i < aTrustedCertificates.length; i++) {
            X509Certificate trustedCert = aTrustedCertificates[i];
            try {
                aCertificate.verify(trustedCert.getPublicKey());
                // Found parent certificate. Certificate is verified to be valid
                return;
            } catch (GeneralSecurityException ex) {
                LOG.warn(".verifyCertificate(aCertificate, aTrustedCertificates)", ex);
                // Certificate is not signed by current trustedCert. Try the
                // next one
            }
        }

        // Certificate is not signed by any of the trusted certs --> it is
        // invalid
        throw new CertificateValidationException("Can not find trusted parent certificate.");
    }

    /**
     * Verifies certification chain using "PKIX" algorithm, defined in RFC-3280.
     * It is considered that the given certification chain start with the target
     * certificate and finish with some root CA certificate. The certification
     * chain is valid if no exception is thrown.
     * 
     * @param aCertChain the certification chain to be verified.
     * @param aTrustedCACertificates a list of most trusted root CA
     *        certificates.
     * @throws CertPathValidatorException if the certification chain is invalid.
     */
    public static void verifyCertificationChain(CertPath aCertChain,
            X509Certificate[] aTrustedCACertificates) throws GeneralSecurityException {
        int chainLength = aCertChain.getCertificates().size();
        if (chainLength < 2) {
            throw new CertPathValidatorException("The certification chain is too "
                    + "short. It should consist of at least 2 certiicates.");
        }

        // Create a set of trust anchors from given trusted root CA certificates
        HashSet<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (int i = 0; i < aTrustedCACertificates.length; i++) {
            TrustAnchor trustAnchor = new TrustAnchor(aTrustedCACertificates[i], null);
            trustAnchors.add(trustAnchor);
        }

        // Create a certification chain validator and a set of parameters for it
        PKIXParameters certPathValidatorParams = new PKIXParameters(trustAnchors);
        certPathValidatorParams.setRevocationEnabled(false);
        CertPathValidator chainValidator = CertPathValidator.getInstance(CERT_CHAIN_VALIDATION_ALGORITHM);

        // Remove the root CA certificate from the end of the chain. This is
        // required
        // by the validation algorithm because by convention the trust anchor
        // certificates should not be a part of the chain that is validated
        CertPath certChainForValidation = removeLastCertFromCertChain(aCertChain);

        // Execute the certification chain validation
        chainValidator.validate(certChainForValidation, certPathValidatorParams);
    }

    /**
     * Removes the last certificate from given certification chain.
     * 
     * @return given cert chain without the last certificate in it.
     */
    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    private static CertPath removeLastCertFromCertChain(CertPath aCertChain) throws CertificateException {
        List certs = aCertChain.getCertificates();
        int certsCount = certs.size();
        List certsWithoutLast = certs.subList(0, certsCount - 1);
        CertificateFactory cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        return cf.generateCertPath(certsWithoutLast);
    }

    /**
     * Exception class for certificate validation errors.
     */
    @SuppressWarnings("serial")
    public static class CertificateValidationException extends GeneralSecurityException {
        public CertificateValidationException(String aMessage) {
            super(aMessage);
        }
    }

    /**
     * @return Base64-encoded ASN.1 DER representation of given X.509
     *         certification chain.
     */
    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    public static String encodeX509CertChainToBase64(
            Certificate[] aCertificationChain) throws CertificateException {
        List certList = Arrays.asList(aCertificationChain);
        CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        CertPath certPath = certFactory.generateCertPath(certList);
        byte[] certPathEncoded = certPath.getEncoded(CERT_CHAIN_ENCODING);
        return Base64.encodeBytes(certPathEncoded);
    }

}
