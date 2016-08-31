package br.com.infox.epp.processo.status;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.componentes.tabs.TabPanel;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;

@Named
@ViewScoped
public class StatusProcessoView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private StatusProcesso statusProcesso;

    @Inject
    private StatusProcessoManager statusProcessoManager;
    @Inject
    private StatusProcessoList statusProcessoList;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isManaged() {
        return statusProcesso != null;
    }

    public void newInstance() {
        setNome(null);
        setDescricao(null);
        statusProcesso=null;
    }

    public void remove(Integer idStatusProcesso) {
        this.statusProcesso = statusProcessoManager.find(idStatusProcesso);
        remove();
    }

    public void remove(StatusProcesso statusProcesso) {
        this.statusProcesso = statusProcesso;
        remove();
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void remove() {
        statusProcessoManager.remove(statusProcesso);
        statusProcessoList.refresh();
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
        setEntityValues();
        statusProcessoManager.update(statusProcesso);
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void persist() {
        this.statusProcesso = new StatusProcesso();
        setEntityValues();
        statusProcessoManager.persist(statusProcesso);
    }

    private void setEntityValues() {
        statusProcesso.setDescricao(getDescricao());
        statusProcesso.setNome(getNome());
    }

    public void edit(StatusProcesso statusProcesso){
        this.statusProcesso = statusProcesso;
        setDescricao(statusProcesso.getDescricao());
        setNome(statusProcesso.getNome());
        ((TabPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("defaultTabPanel")).setActiveTab("form");
    }
    
    public void clickSearchTab() {
        newInstance();
        statusProcessoList.refresh();
    }

    public void clickFormTab() {
        newInstance();
    }

}
