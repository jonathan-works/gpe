package br.com.infox.epp.painel;

import java.util.ArrayList;
import java.util.List;

public class CaixaDefinitionBean implements PanelDefinition {
    
    private Integer idCaixa;
    private String name;
    private List<TaskBean> tasks;
    
    public CaixaDefinitionBean(Integer idCaixa, String name) {
        this.idCaixa = idCaixa;
        this.name = name;
    }

    public void addTaskBean(TaskBean taskBean) {
        if (tasks == null) tasks = new ArrayList<TaskBean>();
        tasks.add(taskBean);
    }
    
    public Integer getIdCaixa() {
        return idCaixa;
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

}
