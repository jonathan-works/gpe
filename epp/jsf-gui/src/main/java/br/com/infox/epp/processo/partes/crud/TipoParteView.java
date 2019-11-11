package br.com.infox.epp.processo.partes.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.list.TipoParteList;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;

@Named
@ViewScoped
public class TipoParteView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoParteManager tipoParteManager;
    @Inject
    private TipoParteList tipoParteList;
    
    private String tab;
    
    private Long id;
    private String identificador;
    private String descricao;
    
    @ExceptionHandled(value = MethodType.PERSIST)
    public void inserir() {
        TipoParte tipoParte = new TipoParte();
        tipoParte.setDescricao(getDescricao());
        tipoParte.setIdentificador(getIdentificador());
        tipoParteManager.persist(tipoParte);
        setId(tipoParte.getId());
    }
    
    @ExceptionHandled(value = MethodType.UPDATE)
    public void atualizar() {
        TipoParte tipoParte = tipoParteManager.find(getId());
        tipoParte.setDescricao(getDescricao());
        tipoParte.setIdentificador(getIdentificador());
        tipoParteManager.update(tipoParte);
    }
    
    public void load(TipoParte tipoParte) {
        setId(tipoParte.getId());
        setIdentificador(tipoParte.getIdentificador());
        setDescricao(tipoParte.getDescricao());
    }
    
    public void onClickFormTab() {
        newInstance();
    }
    
    public void onClickSearchTab() {
        tipoParteList.refresh();
    }
    
    public void newInstance() {
        setId(null);
        setIdentificador(null);
        setDescricao(null);
    }
    
    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIdentificador() {
        return identificador;
    }
    
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
