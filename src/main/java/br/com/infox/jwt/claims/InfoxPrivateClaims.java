package br.com.infox.jwt.claims;

public enum InfoxPrivateClaims implements JWTClaim {
    LOGIN("login",BasicJWTValidators.STRING),
    CPF("cpf",InfoxJWTValidators.CPF);
    public static final String NAMESPACE="http://www.infox.com.br";
    private final String key;
    private final JWTValidator validator;

    private InfoxPrivateClaims(String key, JWTValidator validator) {
        this.key = key;
        this.validator = validator; 
    }
    
    @Override
    public String getClaim(){
        return String.format("%s/%s", NAMESPACE,key);
    }

    @Override
    public JWTValidator validator() {
        return this.validator;
    }
    
}

class InfoxJWTValidators {
    public static final JWTValidator CPF = new JWTValidator(){
        @Override
        public void validate(Object value) {
            if (value == null || String.valueOf(value).trim().isEmpty())
                throw new IllegalArgumentException(String.format("Value [ %s ] is invalid", value));
        }
        
    };
    
    private InfoxJWTValidators(){
    }
}
