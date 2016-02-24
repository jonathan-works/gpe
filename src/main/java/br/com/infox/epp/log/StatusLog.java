package br.com.infox.epp.log;

public enum StatusLog {
    
    ENVIADO("Enviado"), PENDENTE("Pendente de envio"), NENVIADO("Não Enviado");
    
    private String label;
    
    private StatusLog(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

}
