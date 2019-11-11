package br.com.infox.jwt;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import br.com.infox.jwt.claims.JWTClaim;

public class JWT {

    private JWT() {
    }
    
    public static JWTBuilder builder() {
        return new JWTBuilderImpl();
    }

    public static JWTParser parser(){
        return new JWTParserImpl();
    }

    public static Entry<JWTClaim, Object> claim(JWTClaim claim, Object object) {
        return new SimpleImmutableEntry<JWTClaim, Object>(claim, object);
    }

}