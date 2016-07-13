package br.com.infox.jwt;

import java.security.Key;
import java.util.Map;

public interface JWTParser {
    Map<String, Object> parse(String jwt);
    JWTParser setKey(byte[] key);
    JWTParser setKey(Key key);
}
