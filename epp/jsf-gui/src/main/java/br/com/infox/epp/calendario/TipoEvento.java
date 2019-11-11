package br.com.infox.epp.calendario;

public enum TipoEvento {
    F("Feriado"), S("Suspensão de Prazos");
    
    private String label;
    
    private TipoEvento(String label){
        this.label = label;
    }
    
    public String getLabel(){
        return this.label;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
    
}
