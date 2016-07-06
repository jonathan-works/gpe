package br.com.infox.jwt.verifiers;

import java.util.Map;

import com.auth0.jwt.JWTVerifyException;

public interface JWTClaimVerifier {
    void verify(Map<String, Object> payload) throws JWTVerifyException;
}
