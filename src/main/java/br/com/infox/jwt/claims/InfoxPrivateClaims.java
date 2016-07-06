package br.com.infox.jwt.claims;

public enum InfoxPrivateClaims implements JWTClaim {
    LOGIN("login"),
    CPF("cpf");
    public static final String NAMESPACE="http://www.infox.com.br";
    private final String key;

    private InfoxPrivateClaims(String key) {
        this.key = key;
    }
    
    @Override
    public String getClaim(){
        return String.format("%s/%s", NAMESPACE,key);
    }

}
