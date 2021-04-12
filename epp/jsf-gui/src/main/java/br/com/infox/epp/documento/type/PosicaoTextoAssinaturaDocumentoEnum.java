package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum PosicaoTextoAssinaturaDocumentoEnum implements Displayable {

    RODAPE_HORIZONTAL("Rodapé / Horizontal"), LATERAL_VERTICAL("Lateral / Vertical");

    private String label;

    PosicaoTextoAssinaturaDocumentoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}