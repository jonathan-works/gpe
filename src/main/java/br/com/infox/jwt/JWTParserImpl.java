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

import br.com.infox.jwt.encryption.Algorithm;
import io.jsonwebtoken.Jwts;

class JWTParserImpl implements JWTParser {

    private byte[] key;
    
    @Override
    public Map<String, Object> parse(String jwt) {
        Algorithm algorithm = retrieveAlgorithm(jwt);
        switch (algorithm) {
        case HS256:
        case HS384:
        case HS512:
            return Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
        case RS256:
        case RS384:
        case RS512:
            return Jwts.parser().setSigningKey(getValidationKey(key)).parseClaimsJws(jwt).getBody();
        default:
            break;
        }
        throw new IllegalArgumentException(String.format("Invalid JWT string [ %s ]",jwt));
    }

    private Algorithm retrieveAlgorithm(String jwt) {
        String algorithmName = retrieveAlgorithmName(jwt);
        try {
            return Algorithm.valueOf(algorithmName);
        } catch (Exception e){
            throw new IllegalArgumentException(String.format("Unsupported algorithm type [ %s ]", algorithmName),e);
        }
    }

    private String retrieveAlgorithmName(String jwt) {
        try {
            String base64Header = jwt.split("\\.")[0];
            String decodedHeaderJson = new String(Base64.decodeBase64(base64Header), StandardCharsets.UTF_8);
            JsonObject header = new Gson().fromJson(decodedHeaderJson, JsonObject.class);
            return header.get("alg").getAsString();
        } catch (Exception e){
            throw new IllegalArgumentException(String.format("Unable to extract algorithm name from JWT string [ %s ]", jwt),e);
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
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new IllegalArgumentException(String.format("Invalid RSA key [ %s ]", Base64.encodeBase64String(key)), e);
        }
        return validationKey;
    }
    
    @Override
    public JWTParser setKey(String key) {
        this.key = Base64.decodeBase64(key);
        return this;
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