/**
 *
 */
package br.com.infox.epp.certificadoeletronico.builder;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import com.google.common.base.Joiner;

import br.com.infox.certificado.CertificateUtil;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;

/**
 * This class uses the Bouncycastle lightweight API to generate X.509 certificates programmatically.
 *
 * @author abdul
 *
 */
public class CertBuilder extends AbstractCertBuilder {

    @Getter
    public static class CertBuilderDTO{
        private final X509Certificate rootCertificate;
        private final KeyPair rootKP;
        private final String nome;
        private final String cpf;
        private final Date dataNascimento;
        private final String password;
        private final SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");

        public CertBuilderDTO(byte[] rootCertificate, byte[] key, String nome, String cpf,
                Date dataNascimento, String password) {
            super();
            this.rootCertificate = CertUtil.getCertificate(rootCertificate);
            this.rootKP = CertUtil.getKeyPair(key, ROOT_PASS);
            this.nome = nome;
            this.cpf = cpf;
            this.dataNascimento = dataNascimento;
            this.password = password;
            //[0]01012000016019735000000000000011111111111SSSPSE
        }
        public String getTexto() {
            return String.format("%s%s", getDataNascimento(), getCpf());
        }
    }

    private CertBuilderDTO cbDto;


    public CertBuilder(CertBuilderDTO cbDto) {
        this.cbDto = cbDto;
//        this.rootCertificate = CertUtil.getCertificate(cert);
//        this.rootKP = CertUtil.getKeyPair(key, ROOT_PASS);
//        this.password = pass;
    }

    @Override
    public CertificadoDTO gerar() {
        try {
            X500Name x500nameRoot = new JcaX509CertificateHolder(cbDto.getRootCertificate()).getSubject();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
            // Generate a new KeyPair and sign it using the Root Cert Private Key
            // by generating a CSR (Certificate Signing Request)
            X500Name issuedCertSubject = new X500Name(String.format("CN=%s:%s", cbDto.getNome(), cbDto.getCpf()));
            BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));
            KeyPair issuedCertKeyPair = keyPairGenerator.generateKeyPair();


            PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                issuedCertSubject,
                issuedCertKeyPair.getPublic()
            );

            JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
                .setProvider(BC_PROVIDER);

            // Sign the new KeyPair with the root cert Private Key
            ContentSigner signer = csrBuilder.build(cbDto.getRootKP().getPrivate());

            PKCS10CertificationRequest csr = p10Builder.build(signer);

            // Use the Signed KeyPair and CSR to generate an issued Certificate
            // Here serial number is randomly generated. In general, CAs use
            // a sequence to generate Serial number and avoid collisions
            X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(
                x500nameRoot,
                issuedCertSerialNum,
                cbDto.getRootCertificate().getNotBefore(),
                cbDto.getRootCertificate().getNotAfter(),
                csr.getSubject(),
                csr.getSubjectPublicKeyInfo()
            );

//            X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(rootCertIssuer, issuedCertSerialNum, startDate, endDate, csr.getSubject(), csr.getSubjectPublicKeyInfo());


            JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

            // Add Extensions
            // Use BasicConstraints to say that this Cert is not a CA
            issuedCertBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(false)
            );

            // Add Issuer cert identifier as Extension
    //        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootKeyPair.getPublic()));
            issuedCertBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo())
            );



            DLSequence criarAddDadosTitular = criarAddDadosTitular(cbDto.getCpf(), cbDto.getDataNascimento(), null, null, null);

            issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, criarAddDadosTitular);

            // Add intended key usage extension if needed
//            issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));

            // Add DNS name is cert is to used for SSL
//            issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(new ASN1Encodable[] {
//                    new GeneralName(GeneralName.dNSName, "mydomain.local"),
//                    new GeneralName(GeneralName.iPAddress, "127.0.0.1")
//            }));

            X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(signer);

            X509Certificate cert  = new JcaX509CertificateConverter()
                .setProvider(BC_PROVIDER)
                .getCertificate(issuedCertHolder);

//            byte[] encoded = cert.getEncoded();
//
//            try (JcaPEMWriter pw = new JcaPEMWriter(new FileWriter("/tmp/cliente.crt"))) {
//                pw.writeObject(cert);
//            }
//
//            X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(encoded);
//
//            X509Certificate certa  = new JcaX509CertificateConverter()
//                    .setProvider(BC_PROVIDER)
//                    .getCertificate(x509CertificateHolder);

            // Verify the issued cert signature against the root (issuer) cert

            try {
                cert.verify(cbDto.getRootKP().getPublic(), BC_PROVIDER);
            } catch (InvalidKeyException | SignatureException e) {
                throw new BusinessRollbackException("Falha ao gerar o CA", e);
            }

//            generatePemFile(cert, null, "/tmp/cliente.crt");
//
//            generatePemFile(issuedCertKeyPair.getPrivate(), "1234", "/tmp/cliente.pem");


    //        writeCertToFileBase64Encoded(issuedCert, "issued-cert.cer");
    //        exportKeyPairToKeystoreFile("00000000000", "senhaCert", issuedCertKeyPair, issuedCert);,
            return new CertificadoDTO(
                cbDto.getPassword(),
                cbDto.getRootCertificate().getNotBefore(),
                cbDto.getRootCertificate().getNotAfter(),
                generatePemFile(cert, null).toByteArray(),
                generatePemFile(issuedCertKeyPair.getPrivate(), cbDto.getPassword()).toByteArray()
            );
        } catch (NoSuchAlgorithmException | NoSuchProviderException | CertificateException | OperatorCreationException | IOException e) {
            // TODO Auto-generated catch block
            throw new BusinessRollbackException("Falha ao gerar o CA", e);
        }
    }

    private DLSequence criarAddDadosTitular(String cpf, Date dataNascimento, String _pisPasepOuCI, String _nrRG, String _orgaoExpRGComUF) {
        String dataNascimentoStr = leftPad(ofNullable(dataNascimento).map(new SimpleDateFormat("ddMMyyyy")::format).orElse(""),8,' ');
        String pisPasepOuCI = leftPad(trimToEmpty(_pisPasepOuCI), 11, ' ');
        String nrRG = leftPad(trimToEmpty(_nrRG), 15, ' ');
        String orgaoExpRGComUF = leftPad(trimToEmpty(_orgaoExpRGComUF), 10, ' ');

        ASN1ObjectIdentifier oidAtributo = CertificateUtil.OID_DADOS_TITULAR_PF;
        String valorDoAtributo = Joiner.on("").join(dataNascimentoStr, cpf, pisPasepOuCI, nrRG, orgaoExpRGComUF);
        return criaValorNovoAttr(oidAtributo, valorDoAtributo);
    }

    private DLSequence criaValorNovoAttr(ASN1ObjectIdentifier oid, String valor) {
        DEROctetString value = new DEROctetString(valor.getBytes());
        DLSequence otherName = new DLSequence(new ASN1Encodable[] {oid, new DERTaggedObject(GeneralName.otherName, value) });
        GeneralName generalName = new GeneralName(GeneralName.otherName, otherName);
        return new DLSequence(generalName);
    }



}