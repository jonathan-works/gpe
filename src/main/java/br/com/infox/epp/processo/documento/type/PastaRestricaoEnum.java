package br.com.infox.epp.processo.documento.type;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.type.Displayable;

public enum PastaRestricaoEnum implements Displayable {
    
    D("Default"),
    P("Papel"),
    R("Participante"),
    L("Localizacao");

    private String label;
    
    private PastaRestricaoEnum(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public static List<PastaRestricaoEnum> getValuesSemDefault() {
        List<PastaRestricaoEnum> list = new ArrayList<>(3);
        list.add(PastaRestricaoEnum.L);
        list.add(PastaRestricaoEnum.P);
        list.add(PastaRestricaoEnum.R);
        return list;
    }
}
