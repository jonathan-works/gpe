package br.com.infox.jwt.verifiers;

import br.com.infox.jwt.claims.JWTClaim;

public class Verifiers {
    private Verifiers(){
    }
    
    public static JWTClaimVerifier get(JWTClaim jwtClaim){
        return new SingleJWTClaimVerifier(jwtClaim);
    }
    
    public static JWTClaimVerifier allOf(JWTClaim ...jwtClaims){
        return new AllOfJWTClaimVerifier(jwtClaims);
    }
    
    public static JWTClaimVerifier anyOf(JWTClaim ...jwtClaims){
        return new AnyOfJWTClaimVerifier(jwtClaims);
    }
    public static JWTClaimVerifier get(String errorMessagePattern, JWTClaim jwtClaim){
        return new SingleJWTClaimVerifier(errorMessagePattern, jwtClaim);
    }
    
    public static JWTClaimVerifier allOf(String errorMessagePattern, JWTClaim ...jwtClaims){
        return new AllOfJWTClaimVerifier(errorMessagePattern, jwtClaims);
    }
    
    public static JWTClaimVerifier anyOf(String errorMessagePattern, JWTClaim ...jwtClaims){
        return new AnyOfJWTClaimVerifier(errorMessagePattern, jwtClaims);
    }
    
}
