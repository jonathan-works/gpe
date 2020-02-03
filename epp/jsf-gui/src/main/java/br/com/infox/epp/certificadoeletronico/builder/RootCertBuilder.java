package br.com.infox.epp.certificadoeletronico.builder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import br.com.infox.seam.exception.BusinessRollbackException;

public class RootCertBuilder extends AbstractCertBuilder {

    private String infoCertificado;

    public RootCertBuilder(String infoCertificado) {
        this.infoCertificado = infoCertificado;
    }

    @Override
    public CertificadoDTO gerar() {
        // Add the BouncyCastle Provider

        // Initialize a new KeyPair generator
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);

            keyPairGenerator.initialize(2048);


            // Setup start date to yesterday and end date for 1 year validity
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            Date dataInicio = calendar.getTime();

            calendar.add(Calendar.YEAR, 10);
            Date dataFim = calendar.getTime();

            // First step is to create a root certificate
            // First Generate a KeyPair,
            // then a random serial number
            // then generate a certificate using the KeyPair
            KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
            BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

            // Issued By and Issued To same for root certificate
            X500Name rootCertIssuer = new X500Name(infoCertificado);
            X500Name rootCertSubject = new X500Name(infoCertificado);

            ContentSigner rootCertContentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                .setProvider(BC_PROVIDER)
                .build(rootKeyPair.getPrivate());
            X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(
                rootCertIssuer,
                rootSerialNum,
                dataInicio,
                dataFim,
                rootCertSubject,
    //            new X500Name(String.format("CN=%s:%s, %s", "Gustavo", "11111111111", "CN=Infox, O=Infox, C=BR, S=SE")),
                rootKeyPair.getPublic()
            );

            // Add Extensions
            // A BasicConstraint to mark root certificate as CA certificate
            JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
            rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            rootCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, rootCertExtUtils.createAuthorityKeyIdentifier(rootKeyPair.getPublic()));
            rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(rootKeyPair.getPublic()));

            // Create a cert holder and export to X509Certificate
            X509CertificateHolder rootCertHolder = rootCertBuilder.build(rootCertContentSigner);
            X509Certificate rootCert = new JcaX509CertificateConverter()
                .setProvider(BC_PROVIDER)
                .getCertificate(rootCertHolder);


//            generatePemFile(rootCert, null, "/tmp/root.crt");
//
//            generatePemFile(rootKeyPair.getPrivate(), "1234", "/tmp/root.pem");

            return new CertificadoDTO(
                ROOT_PASS,
                dataInicio,
                dataFim,
                generatePemFile(rootCert, null).toByteArray(),
                generatePemFile(rootKeyPair.getPrivate(), ROOT_PASS).toByteArray()
            );
        } catch (NoSuchAlgorithmException | NoSuchProviderException | CertIOException | OperatorCreationException | CertificateException e) {
            // TODO Auto-generated catch block
            throw new BusinessRollbackException("Falha ao gerar o CA", e);
        }
//        writeCertToFileBase64Encoded(rootCert, "root-cert.cer");
//        exportKeyPairToKeystoreFile(ROOT_ALIAS_CERT, "senhaCA", rootKeyPair, rootCert);
    }
}
