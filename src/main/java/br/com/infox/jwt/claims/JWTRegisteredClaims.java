package br.com.infox.jwt.claims;

public enum JWTRegisteredClaims implements JWTClaim {
    EXPIRATION_DATE("exp"),
    ISSUED_AT("iat"),
    JWT_ID("jti"),
    SUBJECT("sub"),
    AUDIENCE("aud"),
    NOT_BEFORE("nbf"),
    ISSUER("iss");
    private final String key;
    private JWTRegisteredClaims(String key) {
        this.key = key;
    }
    
    @Override
    public String getClaim(){
        return key;
    }

}
