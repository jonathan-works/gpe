package br.com.infox.epp.access.type;

import br.com.infox.core.type.Displayable;

public enum UsuarioEnum implements Displayable {
    
    H("Humano"), S("Sistema");

    private String label;
    
    UsuarioEnum(String label) {
        this.label = label;
    }
    
    @Override
    public String getLabel() {
        return this.label;
    }

}
