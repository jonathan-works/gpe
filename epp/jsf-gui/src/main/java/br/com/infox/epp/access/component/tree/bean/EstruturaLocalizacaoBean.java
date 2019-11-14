package br.com.infox.epp.access.component.tree.bean;

public class EstruturaLocalizacaoBean {
    public enum Tipo {
        L, E
    }
    
    private String descricao;
    private Integer id;
    private Tipo tipo;
    
    public EstruturaLocalizacaoBean() {
    }
    
    public EstruturaLocalizacaoBean(Integer id, String descricao, Tipo tipo) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
    }
    
    public EstruturaLocalizacaoBean(Integer id, String descricao, String tipo) {
        this(id, descricao, Tipo.valueOf(tipo));
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return this.descricao;
    }
}
