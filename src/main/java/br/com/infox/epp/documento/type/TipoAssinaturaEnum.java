package br.com.infox.epp.documento.type;

import java.util.HashSet;
import java.util.Set;

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

    public static Set<TipoAssinaturaEnum> getTiposPodeAssinar() {
        Set<TipoAssinaturaEnum> tipos = new HashSet<>();
        tipos.add(TipoAssinaturaEnum.O);
        tipos.add(TipoAssinaturaEnum.F);
        tipos.add(TipoAssinaturaEnum.S);
        return tipos;
    }

    public static Set<TipoAssinaturaEnum> getTiposDeveAssinar() {
        Set<TipoAssinaturaEnum> tipos = new HashSet<>();
        tipos.add(TipoAssinaturaEnum.O);
        tipos.add(TipoAssinaturaEnum.S);
        return tipos;
    }

}
