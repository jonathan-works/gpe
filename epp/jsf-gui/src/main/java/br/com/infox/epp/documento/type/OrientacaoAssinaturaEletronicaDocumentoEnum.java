package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum OrientacaoAssinaturaEletronicaDocumentoEnum implements Displayable {

    RODAPE_HORIZONTAL("Rodapé / Horizontal"), LATERAL_VERTICAL("Lateral / Vertical");

    private String label;

    OrientacaoAssinaturaEletronicaDocumentoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}