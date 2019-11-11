package br.com.infox.jwt.verifiers;

import java.util.Map;

public interface JWTClaimVerifier {
    void verify(Map<String, Object> payload);
}
