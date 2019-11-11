package br.com.infox.security.rsa;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

import br.com.infox.core.exception.SystemExceptionFactory;

public class RSAUtil {
    private RSAUtil() {
    }

    private static byte[] convertBase64ToByteArray(String base64RsaKey) {
        base64RsaKey = base64RsaKey.replaceAll("[-]+(:?BEGIN|END)[^-]+KEY[-]+" + System.lineSeparator() + "?", "");
        return Base64.decodeBase64(base64RsaKey.getBytes(StandardCharsets.UTF_8));
    }

    public static PublicKey getPublicKeyFromBase64(String base64RsaPublicKey) {
        try {
            byte[] decodedPrivateKey = convertBase64ToByteArray(base64RsaPublicKey);
            return getKeyFactory().generatePublic(new X509EncodedKeySpec(decodedPrivateKey));
        } catch (InvalidKeySpecException e) {
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PUBLIC_KEY_STRUCTURE, e);
        }
    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw SystemExceptionFactory.create(RSAErrorCodes.NO_SUCH_ALGORITHM, e);
        }
    }

    public static PrivateKey getPrivateKeyFromBase64(String base64RsaPrivateKey) {
        try {
            byte[] rsaPrivateKeyByteArray = convertBase64ToByteArray(base64RsaPrivateKey);
            RSAPrivateKeySpec keySpec = getRSAPrivateKeySpec(rsaPrivateKeyByteArray);
            return getKeyFactory().generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PRIVATE_KEY_STRUCTURE, e);
        }
    }

    private static RSAPrivateKeySpec getRSAPrivateKeySpec(byte[] decodedPrivateKey) {
        RSAPrivateKeyStructure rsaPrivateKeyStructure = new RSAPrivateKeyStructure(
                getRsaPrivateKeyASN1Sequence(decodedPrivateKey));
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(rsaPrivateKeyStructure.getModulus(),
                rsaPrivateKeyStructure.getPrivateExponent());
        return keySpec;
    }

    private static ASN1Sequence getRsaPrivateKeyASN1Sequence(byte[] decodedPrivateKey) {
        try {
            return (ASN1Sequence) ASN1Sequence.fromByteArray(decodedPrivateKey);
        } catch (IOException e) {
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PUBLIC_KEY_STRUCTURE, e);
        }
    }

}
