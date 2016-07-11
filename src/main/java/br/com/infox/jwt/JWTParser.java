package br.com.infox.jwt;

import java.security.PrivateKey;
import java.util.Map;

public interface JWTParser {
    Map<String, Object> parse(String jwt);
    JWTParser setPrivateKey(String privateKey);
    JWTParser setPrivateKey(byte[] privateKey);
    JWTParser setPrivateKey(PrivateKey privateKey);
}
