package br.com.infox.jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jgroups.util.UUID;
import org.joda.time.LocalDate;

import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.claims.JWTRegisteredClaims;

public class JWTUtilsTeste {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Map<JWTClaim, Object> map = new HashMap<>();
        LocalDate issuedDate = new LocalDate();
        map.put(JWTRegisteredClaims.ISSUED_AT, issuedDate.toDate().getTime());
        map.put(JWTRegisteredClaims.EXPIRATION_DATE, issuedDate.plusDays(30).toDate().getTime());
        map.put(JWTRegisteredClaims.JWT_ID, UUID.randomUUID().toString());
        map.put(JWTRegisteredClaims.SUBJECT, "http://e.tcm.pa.gov.br");
        map.put(JWTRegisteredClaims.AUDIENCE, "http://e.tcm.pa.gov.br/whatever");
        map.put(JWTRegisteredClaims.NOT_BEFORE, issuedDate.minusDays(1).toDate().getTime());
        map.put(JWTRegisteredClaims.ISSUER, "http://www.infox.com.br/epp");
        map.put(InfoxPrivateClaims.LOGIN, "admin");
        
        
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        String signer = JWTUtils.signer(privateKey, map);
        System.out.println("public key ( "+Base64.encodeBase64String(publicKey)+" )");
        System.out.println("private key ( "+Base64.encodeBase64String(privateKey)+" )");
        System.out.println(signer);
    }
}
