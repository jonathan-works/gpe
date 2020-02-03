package br.com.infox.epp.certificadoeletronico.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;

import br.com.infox.seam.exception.BusinessRollbackException;

public abstract class AbstractCertBuilder {

    protected static final String ROOT_PASS = "PA9S8YDP98YP1928DY1P2B8DBP*p(g*DGQ78WG7ODv*&gv&*o";
    protected static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    protected static final String BC_PROVIDER = "BC";
    protected static final String KEY_ALGORITHM = "RSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public abstract CertificadoDTO gerar();

    protected ByteArrayOutputStream generatePemFile(Object privKey, String password) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(JcaPEMWriter pemWrt = new JcaPEMWriter(new OutputStreamWriter(byteArrayOutputStream))) {
            if(password != null) {
                JcaMiscPEMGenerator gen = new JcaMiscPEMGenerator(
                    privKey,
                    new JcePEMEncryptorBuilder("AES-256-CBC").build(password.toCharArray())
                );
                pemWrt.writeObject(gen.generate());
            } else {
                pemWrt.writeObject(privKey);
            }
            return byteArrayOutputStream;
        } catch (IOException e) {
            throw new BusinessRollbackException("Falha ao gerar o arquivo pem", e);
        }
    }

}
