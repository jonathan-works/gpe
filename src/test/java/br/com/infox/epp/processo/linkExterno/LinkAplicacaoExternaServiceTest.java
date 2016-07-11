package br.com.infox.epp.processo.linkExterno;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgroups.util.UUID;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import br.com.infox.core.util.StringUtil;
import br.com.infox.jwt.JWTUtils;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.claims.JWTRegisteredClaims;

public class LinkAplicacaoExternaServiceTest {

    private static String[] URL_VALIDAS_SEM_QUERIES = { "http://www.google.com", "www.google.com",
            "https://www.google.com" };
    private static String[] URL_VALIDAS_COM_QUERIES = { "http://www.google.com?what=123", "www.google.com?what=123",
            "https://www.google.com?what=123" };

    private String generateJWTToken() {
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

        try {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            byte[] privateKey = keyPair.getPrivate().getEncoded();
            return JWTUtils.signer(privateKey, map);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void criarUrlValidas() {
        LinkAplicacaoExternaService service = new LinkAplicacaoExternaService();
        List<Entry<String, String>> queries = new ArrayList<>();
        queries.add(new SimpleImmutableEntry<String, String>("epp.auth.jwt", generateJWTToken()));
        queries.add(new SimpleImmutableEntry<String, String>("quér2", "é algo com um monte de valor inválido"));
        for (String url : URL_VALIDAS_SEM_QUERIES) {
            String treatedURL = service.appendQueriesToUrl(url, queries);
            for (int i = 0, l = queries.size(); i < l; i++) {
                Entry<String, String> query = queries.get(i);
                String failMessage = String.format("Failed to append query [ %s=%s ] to [ %s ]", query.getKey(),
                        query.getValue(), url);
                String encodedKey = StringUtil.encodeToUrlSafeString(query.getKey());
                String encodedValue = StringUtil.encodeToUrlSafeString(query.getValue());
                if (i == 0) {
                    Assert.assertTrue(failMessage,
                            treatedURL.contains(String.format("?%s=%s", encodedKey, encodedValue)));
                } else {
                    Assert.assertTrue(failMessage,
                            treatedURL.contains(String.format("&%s=%s", encodedKey, encodedValue)));
                }
            }
            System.out.println(treatedURL);
        }
        for (String url : URL_VALIDAS_COM_QUERIES) {
            String treatedURL = service.appendQueriesToUrl(url, queries);
            for (int i = 0, l = queries.size(); i < l; i++) {
                Entry<String, String> query = queries.get(i);
                String failMessage = String.format("Failed to append query [ %s=%s ] to [ %s ]", query.getKey(),
                        query.getValue(), url);
                String encodedKey = StringUtil.encodeToUrlSafeString(query.getKey());
                String encodedValue = StringUtil.encodeToUrlSafeString(query.getValue());
                Assert.assertTrue(failMessage, treatedURL.contains(String.format("&%s=%s", encodedKey, encodedValue)));
            }
            System.out.println(treatedURL);
        }
    }

}
