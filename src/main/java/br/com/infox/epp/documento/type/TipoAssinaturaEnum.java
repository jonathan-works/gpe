package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum TipoAssinaturaEnum implements Displayable {
    
    O("Obrigatória"), F("Facultativa"), S("Suficiente"), P("Não Assina");

    private String label;

    TipoAssinaturaEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
