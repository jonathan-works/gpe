package br.com.infox.jwt;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.jgroups.util.UUID;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTRegisteredClaims;
import br.com.infox.jwt.encryption.Algorithm;
import io.jsonwebtoken.SignatureException;

public class JWTBuilderTeste {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        LocalDateTime issuedDate = new LocalDateTime();
        
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        JWTBuilder jwtBuilder2 = JWT.builder().setPrivateKey(privateKey);
        jwtBuilder2 = jwtBuilder2.setAlgorithm(Algorithm.RS256);
        jwtBuilder2.addClaim(JWTRegisteredClaims.ISSUED_AT, issuedDate.toDate().getTime()/1000);
        jwtBuilder2.addClaim(JWTRegisteredClaims.EXPIRATION_DATE, issuedDate.plusDays(30).toDate().getTime()/1000);
        jwtBuilder2.addClaim(JWTRegisteredClaims.JWT_ID, UUID.randomUUID().toString());
        jwtBuilder2.addClaim(JWTRegisteredClaims.SUBJECT, "http://e.tcm.pa.gov.br");
        jwtBuilder2.addClaim(JWTRegisteredClaims.AUDIENCE, "http://e.tcm.pa.gov.br/whatever");
        jwtBuilder2.addClaim(JWTRegisteredClaims.NOT_BEFORE, issuedDate.minusDays(1).toDate().getTime()/1000);
        jwtBuilder2.addClaim(JWTRegisteredClaims.ISSUER, "http://www.infox.com.br/epp");
        jwtBuilder2.addClaim(InfoxPrivateClaims.LOGIN, "admin");
        String build = jwtBuilder2.build();
        
        System.out.println("public key ( "+Base64.encodeBase64String(publicKey)+" )");
        System.out.println("private key ( "+Base64.encodeBase64String(privateKey)+" )");
        System.out.println(build);
        for (Entry<String, Object> entry : JWT.parser().setKey(keyPair.getPublic()).parse(build).entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        
    }
    
    private void parseAndPrint(String JWTString, JWTParser parser) {
        for (Entry<String, Object> entry : parser.parse(JWTString).entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("VALID STRING ( "+JWTString+" )");
    }

    @Test
    public void testarAssinaturasValidas() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        for (Algorithm algorithm : Algorithm.values()) {
            LocalDateTime issuedDate = new LocalDateTime();
            JWTBuilder jwtBuilder = JWT.builder().setPrivateKey(privateKey);
            jwtBuilder = jwtBuilder.setAlgorithm(algorithm);
            jwtBuilder.addClaim(JWTRegisteredClaims.ISSUED_AT, issuedDate.toDate().getTime()/1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.EXPIRATION_DATE, issuedDate.plusDays(30).toDate().getTime()/1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.JWT_ID, UUID.randomUUID().toString());
            jwtBuilder.addClaim(JWTRegisteredClaims.SUBJECT, "http://e.tcm.pa.gov.br");
            jwtBuilder.addClaim(JWTRegisteredClaims.AUDIENCE, "http://e.tcm.pa.gov.br/whatever");
            jwtBuilder.addClaim(JWTRegisteredClaims.NOT_BEFORE, issuedDate.minusDays(1).toDate().getTime()/1000);
            jwtBuilder.addClaim(JWTRegisteredClaims.ISSUER, "http://www.infox.com.br/epp");
            jwtBuilder.addClaim(InfoxPrivateClaims.LOGIN, "admin");
            String jwtString = jwtBuilder.build();
            try {
                JWT.parser().setKey(privateKey).parse(jwtString);
                if (algorithm.name().startsWith("RS"))
                    JWT.parser().setKey(publicKey).parse(jwtString);
            } catch (SignatureException e){
                Assert.fail(String.format("Failed to assert signature with standard byte array signature for jwt string [ %s ]", jwtString));
            }
            try {
                JWT.parser().setKey(keyPair.getPrivate()).parse(jwtString);
                if (algorithm.name().startsWith("RS"))
                    JWT.parser().setKey(keyPair.getPublic()).parse(jwtString);
            } catch (SignatureException e){
                Assert.fail(String.format("Failed to assert signature with %s for jwt string [ %s ]", Key.class.getName(), jwtString));
            }
            try {
                JWT.parser().setKey(Base64.encodeBase64String(privateKey)).parse(jwtString);
                if (algorithm.name().startsWith("RS"))
                    JWT.parser().setKey(Base64.encodeBase64String(publicKey)).parse(jwtString);
            } catch (SignatureException e){
                Assert.fail(String.format("Failed to assert signature with base64 byte array for jwt string [ %s ]", jwtString));
            }
        }
    }
    
}
