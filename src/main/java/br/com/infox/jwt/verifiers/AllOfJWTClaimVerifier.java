package br.com.infox.jwt.verifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import br.com.infox.jwt.claims.JWTClaim;

public class AllOfJWTClaimVerifier implements JWTClaimVerifier {
    
    private final List<JWTClaim> jwtClaims;
    private final String errorMessagePattern;
    
    AllOfJWTClaimVerifier(JWTClaim... jwtClaims){
        this("JWT Token expected to have all of the following missing claims [ %s ]", jwtClaims);
    }
    
    AllOfJWTClaimVerifier(String errorMessagePattern, JWTClaim... jwtClaims){
        this.jwtClaims = Arrays.asList(jwtClaims);
        this.errorMessagePattern = errorMessagePattern;
    }
    
    @Override
    public void verify(Map<String, Object> payload) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (JWTClaim jwtClaim : jwtClaims) {
            String key = jwtClaim.getClaim();
            if (!payload.containsKey(key)){
                if (!first){
                    sb.append(", ");
                }
                sb.append(key);
                first=false;
            }
        }
        if (!sb.toString().trim().isEmpty()){
            throw new IllegalArgumentException(String.format(errorMessagePattern, sb.toString()));
        }
    }

}
