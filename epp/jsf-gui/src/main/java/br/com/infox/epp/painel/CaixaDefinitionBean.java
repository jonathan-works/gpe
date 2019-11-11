package br.com.infox.epp.painel;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.epp.painel.caixa.Caixa;

public class CaixaDefinitionBean implements PanelDefinition {
    
    private Integer idCaixa;
    private String name;
    private PanelDefinition parentDefinition;
    private List<TaskBean> tasks = new ArrayList<>();
    
    public CaixaDefinitionBean(Integer idCaixa, String name, PanelDefinition parentDefinition) {
        this.idCaixa = idCaixa;
        this.name = name;
        this.parentDefinition = parentDefinition;
    }

    public void addTaskBean(TaskBean taskBean) {
        tasks.add(taskBean);
    }
    
    public Integer getIdCaixa() {
        return idCaixa;
    }
    
    public PanelDefinition getParentDefinition() {
        return parentDefinition;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getQuantidade() {
        return tasks == null ? 0 : tasks.size();
    }

    @Override
    public List<TaskBean> getTasks() {
        return tasks;
    }

    @Override
    public Object getId() {
        return idCaixa;
    }

    @Override
    public void moverParaCaixa(TaskBean taskBean, Caixa caixa) {
        tasks.remove(taskBean);
        getParentDefinition().moverParaCaixa(taskBean, caixa);
    }

}
