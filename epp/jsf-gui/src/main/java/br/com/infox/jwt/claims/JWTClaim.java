package br.com.infox.jwt.claims;

public interface JWTClaim {

    String getClaim();
    JWTValidator validator();
}