package br.com.infox.jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.jgroups.util.UUID;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.claims.JWTRegisteredClaims;
import br.com.infox.jwt.encryption.Algorithm;

public class JWTUtilsTeste {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        LocalDateTime issuedDate = new LocalDateTime();
        
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        JWTBuilder jwtBuilder2 = Jwt.builder().setPrivateKey(privateKey);
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
        for (Entry<String, Object> entry : Jwt.parser().setPrivateKey(keyPair.getPrivate()).parse(build).entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        
    }
}
