package br.com.infox.jwt;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.encryption.Algorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Jwt implements JWTBuilder {

    private byte[] privateKey;
    private Map<JWTClaim, Object> claims;
    private Algorithm algorithm;

    private Jwt() {
        claims = new HashMap<>();
        try {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            setPrivateKey(keyPair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            byte[] privateKey = new byte[2048];
            new SecureRandom().nextBytes(privateKey);
            setPrivateKey(privateKey);
        }
    }

    @Override
    public String build() {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam("typ","JWT");
        for (Entry<JWTClaim, Object> claim : claims.entrySet()) {
            JWTClaim jwtClaim = claim.getKey();
            Object value = claim.getValue();
            jwtClaim.validator().validate(value);
            builder = builder.claim(jwtClaim.getClaim(), value);
        }
        switch (algorithm) {
        case HS384:
            return builder.signWith(SignatureAlgorithm.HS384, this.privateKey).compact();
        case HS512:
            return builder.signWith(SignatureAlgorithm.HS512, this.privateKey).compact();
        case RS256:
            return builder.signWith(SignatureAlgorithm.RS256, getPrivateKey()).compact();
        case RS384:
            return builder.signWith(SignatureAlgorithm.RS384, getPrivateKey()).compact();
        case RS512:
            return builder.signWith(SignatureAlgorithm.RS512, getPrivateKey()).compact();
        default:
            return builder.signWith(SignatureAlgorithm.HS256, this.privateKey).compact();
        }
    }

    private PrivateKey getPrivateKey(){
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(this.privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new IllegalStateException("NOT A VALID RSA KEY");
        }
    }
    
    public static JWTBuilder builder() {
        return new Jwt();
    }

    @Override
    public JWTBuilder setPrivateKey(String privateKey) {
        this.privateKey = privateKey.getBytes(StandardCharsets.UTF_8);
        return this;
    }

    @Override
    public JWTBuilder setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public JWTBuilder setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey.getEncoded();
        return this;
    }

    @Override
    public JWTBuilder addClaim(JWTClaim claim, Object value) {
        this.claims.put(claim, value);
        return this;
    }

    @Override
    public JWTBuilder setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public static JWTParser parser(){
        return new JwtParserImpl();
    }

}

class JWTSigner {
    byte[] privateKey;
    Map<JWTClaim, Object> claims;
    Algorithm algorithm;
    
    String encodedPayload() {
        JsonObject payload = new JsonObject();
        Gson gson = new Gson();
        for (Entry<JWTClaim, Object> entry : claims.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            entry.getKey().validator().validate(entry.getValue());
            payload.add(entry.getKey().getClaim(), gson.toJsonTree(entry.getValue()));
        }
        return Base64.encodeBase64URLSafeString(new Gson().toJson(payload).getBytes(StandardCharsets.UTF_8));
    }

    String encodedHeader() {
        if (algorithm == null) { // default the algorithm if not specified
            algorithm = Algorithm.HS256;
        }
        JsonObject header = new JsonObject();
        // create the header
        header.addProperty("typ", "JWT");
        header.addProperty("alg", algorithm.name());
        return Base64.encodeBase64URLSafeString(new Gson().toJson(header).getBytes(StandardCharsets.UTF_8));
    }

    String encodedSignature(String unsignedJWT) {
        byte[] signature = null;
        switch (algorithm) {
        case HS256:
        case HS384:
        case HS512:
            signature = signHmac(algorithm, unsignedJWT, privateKey);
            break;
        case RS256:
        case RS384:
        case RS512:
            signature = signRSA(algorithm, unsignedJWT, privateKey);
            break;
        default:
            throw new IllegalArgumentException("Unsupported signing method");
        }
        return Base64.encodeBase64URLSafeString(signature);
    }

    static byte[] signRSA(Algorithm algorithm, String msg, byte[] secretKey) {
        try {
            Signature signature = Signature.getInstance(algorithm.getValue());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(secretKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey pvtKey = keyFactory.generatePrivate(privateKeySpec);

            signature.initSign(pvtKey);
            byte[] bytesToSign = msg.getBytes(StandardCharsets.UTF_8);
            signature.update(bytesToSign);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] digested = digest.digest(bytesToSign);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pvtKey);
            return cipher.doFinal(digested);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported arguments", e);
        }
    }

    static byte[] signHmac(Algorithm algorithm, String msg, byte[] secret) {
        try {
            Mac mac = Mac.getInstance(algorithm.getValue());
            mac.init(new SecretKeySpec(secret, algorithm.getValue()));
            return mac.doFinal(msg.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Unsupported arguments", e);
        }
    }

}