package br.com.infox.jwt.claims;

public interface JWTValidator {
    void validate(Object value);
}
