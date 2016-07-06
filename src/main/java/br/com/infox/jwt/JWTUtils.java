package br.com.infox.jwt;

import static br.com.infox.jwt.claims.JWTRegisteredClaims.EXPIRATION_DATE;
import static br.com.infox.jwt.claims.JWTRegisteredClaims.ISSUED_AT;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.verifiers.JWTClaimVerifier;
import br.com.infox.jwt.verifiers.Verifiers;

public class JWTUtils {
    
    public static String signer(byte[] privateKey, Map<JWTClaim,Object> payload) throws NoSuchAlgorithmException{
        JWTSigner signer = new JWTSigner(privateKey);
        Map<String,Object> claims = new HashMap<>();
        for (Entry<JWTClaim, Object> entry : payload.entrySet()) {
            claims.put(entry.getKey().getClaim(), entry.getValue());
        }
        Options options=new Options();
        return signer.sign(claims, options);
    }
    
    public static Map<String, Object> verify(byte[] secret, String jwt, Collection<JWTClaimVerifier> mandatoryKeyVerifiers) throws JWTVerifyException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException{
        Map<String, Object> decodedPayload = new JWTVerifier(secret).verify(jwt);
        Set<JWTClaimVerifier> keyVerifiers = new HashSet<>();
        
        keyVerifiers.add(Verifiers.allOf(EXPIRATION_DATE,ISSUED_AT));
        keyVerifiers.addAll(mandatoryKeyVerifiers);
        
        for (JWTClaimVerifier jwtKeyVerifier : keyVerifiers) {
            jwtKeyVerifier.verify(decodedPayload);
        }
        return decodedPayload;
    }
    
}
