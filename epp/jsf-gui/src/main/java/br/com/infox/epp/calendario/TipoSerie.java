package br.com.infox.epp.calendario;

import br.com.infox.core.type.Displayable;

public enum TipoSerie implements Displayable {
    A("Anual");
    
    private String label;

    private TipoSerie(String label){
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
    
}
