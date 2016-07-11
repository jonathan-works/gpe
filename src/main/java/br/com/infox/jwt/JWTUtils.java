package br.com.infox.jwt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Map;

import br.com.infox.jwt.claims.JWTClaim;
import br.com.infox.jwt.verifiers.JWTClaimVerifier;

public class JWTUtils {
    
    public static String signer(byte[] privateKey, Map<JWTClaim,Object> payload) throws NoSuchAlgorithmException{
//        JWTSigner signer = new JWTSigner(privateKey);
//        Map<String,Object> claims = new HashMap<>();
//        for (Entry<JWTClaim, Object> entry : payload.entrySet()) {
//            claims.put(entry.getKey().getClaim(), entry.getValue());
//        }
//        Options options=new Options();
//        return signer.sign(claims, options);
        return null;
    }
    
    public static Map<String, Object> verify(byte[] secret, String jwt, Collection<JWTClaimVerifier> mandatoryKeyVerifiers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException{
//        Map<String, Object> decodedPayload = new JWTVerifier(secret).verify(jwt);
//        Set<JWTClaimVerifier> keyVerifiers = new HashSet<>();
//        
//        keyVerifiers.add(Verifiers.allOf(EXPIRATION_DATE,ISSUED_AT));
//        keyVerifiers.addAll(mandatoryKeyVerifiers);
//        
//        for (JWTClaimVerifier jwtKeyVerifier : keyVerifiers) {
//            jwtKeyVerifier.verify(decodedPayload);
//        }
//        return decodedPayload;
        return null;
    }
    
}
