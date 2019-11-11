package br.com.infox.jwt;

import java.security.PrivateKey;

import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.encryption.Algorithm;

public interface JWTBuilder {

    String build();

    JWTBuilder addClaim(JWTClaim claim, Object value);
    JWTBuilder setAlgorithm(Algorithm algorithm);
    JWTBuilder setPrivateKey(byte[] privateKey);
    JWTBuilder setPrivateKey(PrivateKey privateKey);
}