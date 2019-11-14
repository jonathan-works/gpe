package br.com.infox.jwt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.io.IOUtils;
import org.jgroups.util.UUID;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

import br.com.infox.core.exception.SystemException;
import br.com.infox.core.net.UrlBuilder;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTRegisteredClaims;
import br.com.infox.jwt.encryption.Algorithm;
import br.com.infox.security.rsa.RSAUtil;

public class JWTBuilderTeste {
    
    private static String getBase64ByteArray(InputStream stream) throws IOException{
        String string = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
        System.out.println(string);
        return string;
    }
    
    public static PrivateKey privKey(InputStream stream) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException{
        return RSAUtil.getPrivateKeyFromBase64(getBase64ByteArray(stream));
    }
    
    public static PublicKey pubKey(InputStream stream) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException{
        return RSAUtil.getPublicKeyFromBase64(getBase64ByteArray(stream));
    }
    
    public static InputStream fileToInputStream(String file){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(file);
    }
    public static PublicKey pubKey(String file) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException{
        return pubKey(fileToInputStream(file));
    }
    public static PrivateKey privKey(String file) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException{
        return privKey(fileToInputStream(file));
    }
    
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        PrivateKey privKey = privKey("rsa-keypair/jwt_rsa");
        PublicKey pubKey = pubKey("rsa-keypair/jwt_rsa_pub");
        
        
        JWTBuilder jwtBuilder = JWT.builder().setPrivateKey(privKey);
        jwtBuilder = jwtBuilder.setAlgorithm(Algorithm.RS256);
        jwtBuilder.addClaim(InfoxPrivateClaims.LOGIN, "erikliberal");
        LocalDateTime issuedDate = new LocalDateTime();
        System.out.println(new UrlBuilder("localhost:8080")
                .path("epp").path("rest").path("usuario").path("signin")
                .query("epp.auth.jwt",jwtBuilder.build()).build());
    }
    
    @Test
    public void testarAssinaturasValidas() throws Exception {
        PrivateKey privKey = privKey("rsa-keypair/jwt_rsa");
        PublicKey pubKey = pubKey("rsa-keypair/jwt_rsa_pub");
        KeyPair keyPair = new KeyPair(pubKey, privKey);
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        for (Algorithm algorithm : Algorithm.values()) {
            LocalDateTime issuedDate = new LocalDateTime();
            JWTBuilder jwtBuilder = JWT.builder().setPrivateKey(privateKey);
            jwtBuilder = jwtBuilder.setAlgorithm(algorithm);
            jwtBuilder.addClaim(JWTRegisteredClaims.ISSUED_AT, issuedDate.toDate().getTime() / 1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.EXPIRATION_DATE, issuedDate.plusDays(30).toDate().getTime() / 1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.JWT_ID, UUID.randomUUID().toString());
            jwtBuilder.addClaim(JWTRegisteredClaims.SUBJECT, "http://e.tcm.pa.gov.br");
            jwtBuilder.addClaim(JWTRegisteredClaims.AUDIENCE, "http://e.tcm.pa.gov.br/whatever");
            jwtBuilder.addClaim(JWTRegisteredClaims.NOT_BEFORE, issuedDate.minusDays(1).toDate().getTime() / 1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.ISSUER, "http://www.infox.com.br/epp");
            jwtBuilder.addClaim(InfoxPrivateClaims.LOGIN, "admin");
            String jwtString = jwtBuilder.build();
            System.out.println(jwtString);
            try {
                JWT.parser().setKey(privateKey).parse(jwtString);
                if (algorithm.name().startsWith("RS"))
                    JWT.parser().setKey(publicKey).parse(jwtString);
            } catch (SystemException e) {
                if (JWTErrorCodes.SIGNATURE_VERIFICATION_ERROR.equals(e.getErrorCode())
                        || JWTErrorCodes.INVALID_RSA_KEY.equals(e.getErrorCode()))
                    Assert.fail(String.format(
                            "Failed to assert signature with standard byte array signature for jwt string [ %s ]",
                            jwtString));
                Assert.fail(e.getMessage());
            }
            try {
                JWT.parser().setKey(keyPair.getPrivate()).parse(jwtString);
                if (algorithm.name().startsWith("RS"))
                    JWT.parser().setKey(keyPair.getPublic()).parse(jwtString);
            } catch (SystemException e) {
                if (JWTErrorCodes.SIGNATURE_VERIFICATION_ERROR.equals(e.getErrorCode())
                        || JWTErrorCodes.INVALID_RSA_KEY.equals(e.getErrorCode()))
                    Assert.fail(String.format("Failed to assert signature with %s for jwt string [ %s ]",
                            Key.class.getName(), jwtString));
                Assert.fail(e.getMessage());
            }
        }
    }

}
