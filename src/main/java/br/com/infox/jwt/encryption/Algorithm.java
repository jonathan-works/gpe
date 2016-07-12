package br.com.infox.jwt.encryption;

public enum Algorithm {
    HS256("HmacSHA256"), HS384("HmacSHA384"), HS512("HmacSHA512"), 
    RS256("SHA256withRSA"), RS384("SHA384withRSA"), RS512("SHA512withRSA");

    private Algorithm(String value) {
            this.value = value;
    }
    
    private String value;

    public String getValue() {
            return value;
    }
}