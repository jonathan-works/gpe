package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum LocalizacaoAssinaturaEletronicaDocumentoEnum implements Displayable {

    PRIMEIRA_PAGINA("Primeira página"), ULTIMA_PAGINA("Última página"), TODAS_PAGINAS("Todas as páginas"),
    PAGINA_UNICA("Informar página única");

    private String label;

    LocalizacaoAssinaturaEletronicaDocumentoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}