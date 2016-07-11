package br.com.infox.jwt;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

class JwtParserImpl implements JWTParser {

    private String privateKeyBase64;
    private PrivateKey privateKeyRSA;
    private byte[] privateKey;

    @Override
    public Map<String, Object> parse(String jwt) {
        JwtParser parser = Jwts.parser();
        if (privateKeyBase64 != null) {
            parser = parser.setSigningKey(privateKeyBase64);
        } else if (privateKeyRSA != null){
            parser = parser.setSigningKey(privateKeyRSA);
        } else {
            parser = parser.setSigningKey(privateKey);
        }
        return parser.parseClaimsJws(jwt).getBody();
    }

    @Override
    public JWTParser setPrivateKey(String privateKey) {
        this.privateKeyBase64 = privateKey;
        return this;
    }

    @Override
    public JWTParser setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public JWTParser setPrivateKey(PrivateKey privateKey) {
        this.privateKeyRSA = privateKey;
        return this;
    }
    
}