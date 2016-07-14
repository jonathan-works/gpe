package br.com.infox.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.core.exception.SystemExceptionFactory;
import br.com.infox.jwt.encryption.Algorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

class JWTParserImpl implements JWTParser {

    private byte[] key;
    
    @Override
    public Map<String, Object> parse(String jwt) {
        Algorithm algorithm = retrieveAlgorithm(jwt);
        JwtParser jwtParser = Jwts.parser();
        switch (algorithm) {
        case HS256:
        case HS384:
        case HS512:
            jwtParser = jwtParser.setSigningKey(key);
            break;
        case RS256:
        case RS384:
        case RS512:
            jwtParser = jwtParser.setSigningKey(getValidationKey(key));
            break;
        default:
            throw SystemExceptionFactory.create(JWTErrorCodes.UNSUPPORTED_ALGORITHM);
        }
        try {
            return jwtParser.parseClaimsJws(jwt).getBody();
        } catch (SignatureException e){
            throw SystemExceptionFactory.create(JWTErrorCodes.SIGNATURE_VERIFICATION_ERROR, e);
        }
    }

    private Algorithm retrieveAlgorithm(String jwt) {
        String algorithmName = retrieveAlgorithmName(jwt);
        try {
            return Algorithm.valueOf(algorithmName);
        } catch (Exception e){
            throw SystemExceptionFactory.create(JWTErrorCodes.UNSUPPORTED_ALGORITHM);
        }
    }

    private String retrieveAlgorithmName(String jwt) {
        try {
            String base64Header = jwt.split("\\.")[0];
            String decodedHeaderJson = new String(Base64.decodeBase64(base64Header), StandardCharsets.UTF_8);
            JsonObject header = new Gson().fromJson(decodedHeaderJson, JsonObject.class);
            return header.get("alg").getAsString();
        } catch (Exception e){
            throw SystemExceptionFactory.create(JWTErrorCodes.FAILED_TO_PARSE_ALGORITHM);
        }
    }

    private Key getValidationKey(byte[] key) {
        Key validationKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            try {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
                validationKey = keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
              validationKey=keyFactory.generatePublic(keySpec);
            }
        } catch (InvalidKeySpecException e){
            throw SystemExceptionFactory.create(JWTErrorCodes.INVALID_RSA_KEY, e)
                .set("base64Key", Base64.encodeBase64String(key));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("failed to load 'RSA' keyfactory instance",e);
        }
        return validationKey;
    }

    @Override
    public JWTParser setKey(byte[] key) {
        this.key = key;
        return this;
    }

    @Override
    public JWTParser setKey(Key key) {
        this.key = key.getEncoded();
        return this;
    }
    
}