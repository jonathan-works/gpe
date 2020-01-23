package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum TipoMeioAssinaturaEnum implements Displayable {

    E("Eletrônica"), T("Token");

    private String label;

    TipoMeioAssinaturaEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public static TipoMeioAssinaturaEnum fromName(String name) {
    	for(TipoMeioAssinaturaEnum value : values()) {
    		if(value.name().equals(name)) {
    			return value;
    		}
    	}
    	return null;
    }

    @Override
    public String toString() {
        return getLabel();
    }

}
