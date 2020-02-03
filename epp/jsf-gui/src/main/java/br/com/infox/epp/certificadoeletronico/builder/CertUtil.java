package br.com.infox.epp.certificadoeletronico.builder;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import br.com.infox.seam.exception.BusinessRollbackException;

public class CertUtil {

    protected static final String BC_PROVIDER = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate getCertificate(byte[] filename)  {
        return getCertificate(new InputStreamReader(new ByteArrayInputStream(filename)));
    }

    public static X509Certificate getCertificate(String filename)  {
        try {
            return getCertificate(new FileReader(filename));
        } catch (FileNotFoundException e) {
            throw new BusinessRollbackException(String.format("Arquivo não encontrado: %s", filename), e);
        }
    }

    public static X509Certificate getCertificate(InputStreamReader isr)  {
        try (PEMParser pemParser = new PEMParser(isr)) {
            X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) pemParser.readObject();
            return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(x509CertificateHolder);
        } catch (IOException | CertificateException e) {
            throw new BusinessRollbackException("Falha ao retornar o cerfificado", e);
        }
    }


    public static KeyPair getKeyPair(byte[] filename, String pass)  {
        return getKeyPair(new InputStreamReader(new ByteArrayInputStream(filename)), pass);
    }

    public static KeyPair getKeyPair(String filename, String pass)  {
        try {
            return getKeyPair(new FileReader(filename), pass);
        } catch (FileNotFoundException e) {
            throw new BusinessRollbackException(String.format("Arquivo não encontrado: %s", filename), e);
        }
    }

    public static KeyPair getKeyPair(InputStreamReader isr, String pass)  {

        try (PEMParser pemParser = new PEMParser(isr)){
         // reads your key file
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            KeyPair kp;

//            JceOpenSSLPKCS8DecryptorProviderBuilder jceOpenSSLPKCS8DecryptorProviderBuilder = new JceOpenSSLPKCS8DecryptorProviderBuilder();
            if (object instanceof PEMEncryptedKeyPair) {
                PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(pass.toCharArray());
                // Encrypted key - we will use provided password
                PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
                // uses the password to decrypt the key
                kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
            } else {
                // Unencrypted key - no password needed
                PEMKeyPair ukp = (PEMKeyPair) object;
                kp = converter.getKeyPair(ukp);
            }

            // RSA
//            KeyFactory keyFac = KeyFactory.getInstance("RSA");
//            RSAPrivateCrtKeySpec privateKey = keyFac.getKeySpec(kp.getPrivate(), RSAPrivateCrtKeySpec.class);
//
//            System.out.println(privateKey.getClass());
            return kp;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new BusinessRollbackException("Falha ao retornar keypair", e);
        }
    }

}
