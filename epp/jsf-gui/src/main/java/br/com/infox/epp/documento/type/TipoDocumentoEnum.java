package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum TipoDocumentoEnum implements Displayable {

    P("Texto"), D("Anexo"), T("Todos");

    private String label;

    TipoDocumentoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
