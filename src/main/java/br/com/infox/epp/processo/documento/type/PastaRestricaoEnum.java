package br.com.infox.epp.processo.documento.type;

public enum PastaRestricaoEnum {
    
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
}
