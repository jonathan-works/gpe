package br.com.infox.ibpm.type;

public enum PooledActorType {
    
    USER("user"), GROUP("group"), LOCAL("local");
    
    private String value;
    
    private PooledActorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public String toPooledActorId(String actorId) {
        return value + ":" + actorId; 
    }
}
