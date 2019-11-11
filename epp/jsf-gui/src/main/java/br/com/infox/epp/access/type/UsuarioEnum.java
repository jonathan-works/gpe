package br.com.infox.epp.access.type;

import br.com.infox.core.type.Displayable;

public enum UsuarioEnum implements Displayable {

    S("Não faz login"), C("Certificado Digital"), P("Senha"), H("Senha e Certificado Digital");

    private String label;

    private UsuarioEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
