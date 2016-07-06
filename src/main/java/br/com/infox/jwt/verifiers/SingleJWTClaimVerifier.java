package br.com.infox.jwt.verifiers;

import java.util.Map;

import com.auth0.jwt.JWTVerifyException;

import br.com.infox.jwt.claims.JWTClaim;

public class SingleJWTClaimVerifier implements JWTClaimVerifier {

    private final String errorMessagePattern;
    private final JWTClaim claim;
    SingleJWTClaimVerifier(JWTClaim claim){
        this("JWT Token expected to have the following missing claim [ %s ]", claim);
    }
    SingleJWTClaimVerifier(String errorMessagePattern,JWTClaim claim) {
        this.errorMessagePattern = errorMessagePattern;
        this.claim=claim;
    }


    public void verify(Map<String, Object> payload) throws JWTVerifyException {
        if (!payload.containsKey(claim.getClaim())) {
            throw new JWTVerifyException(String.format(errorMessagePattern, claim.getClaim()));
        }
    }
}
