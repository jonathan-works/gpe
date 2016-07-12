package br.com.infox.jwt;

public class JWT {

    private JWT() {
    }
    
    public static JWTBuilder builder() {
        return new JWTBuilderImpl();
    }

    public static JWTParser parser(){
        return new JWTParserImpl();
    }

}