package br.com.infox.epp.painel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infox.epp.painel.caixa.Caixa;

public class TaskDefinitionBean implements PanelDefinition {
    
    private String taskNodeKey;
    private String name;
    private List<TaskBean> tasks = new ArrayList<>();
    
    private Map<Integer, CaixaDefinitionBean> caixas = new HashMap<>();

    public TaskDefinitionBean(String taskNodeKey, String name) {
        this.taskNodeKey = taskNodeKey;
        this.name = name;
    }

    public TaskDefinitionBean(String taskNodeKey, String name, List<TaskBean> tasks) {
        this.taskNodeKey = taskNodeKey;
        this.name = name;
        this.tasks = tasks;
    }

    public void addTaskBean(TaskBean taskBean) {
        if (taskBean.getIdCaixa() != null) {
            addTaskBeanToCaixa(taskBean);
        } else {
            tasks.add(taskBean);
        }
    }
    
    private void addTaskBeanToCaixa(TaskBean taskBean) {
        CaixaDefinitionBean caixaDefinitionBean = caixas.get(taskBean.getIdCaixa());
        if (caixaDefinitionBean == null) {
            caixaDefinitionBean = new CaixaDefinitionBean(taskBean.getIdCaixa(), taskBean.getNomeCaixa(), this);
            caixas.put(taskBean.getIdCaixa(), caixaDefinitionBean);
        }
        caixaDefinitionBean.addTaskBean(taskBean);
    }

    public String getTaskNodeKey() {
        return taskNodeKey;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<TaskBean> getTasks() {
        return tasks;
    }

    @Override
    public int getQuantidade() {
        return tasks == null ? 0 : tasks.size();
    }
    
    public void removerCaixa(Integer idCaixa) {
        CaixaDefinitionBean caixaDefinitionBean = caixas.get(idCaixa);
        if (caixaDefinitionBean == null) return;
        for (TaskBean taskBean : caixaDefinitionBean.getTasks()) {
            taskBean.removerCaixa();
        }
        tasks.addAll(caixaDefinitionBean.getTasks());
        caixas.remove(idCaixa);
    }
    
    public void moverParaCaixa(TaskBean taskBean, Caixa caixa) {
        tasks.remove(taskBean);
        taskBean.moverParaCaixa(caixa);
        CaixaDefinitionBean caixaDefinitionBean = caixas.get(caixa.getIdCaixa());
        if (caixaDefinitionBean == null) {
            caixaDefinitionBean = new CaixaDefinitionBean(caixa.getIdCaixa(), caixa.getNomeCaixa(), this);
            caixas.put(caixa.getIdCaixa(), caixaDefinitionBean);
        }
        caixaDefinitionBean.addTaskBean(taskBean);
    }
    
    public CaixaDefinitionBean getCaixaDefinitionBean(Integer idCaixa) {
        return caixas.get(idCaixa);
    }
    
    public int getQuantidadeEmCaixa() {
        int sum = 0;
        for (CaixaDefinitionBean caixaDefinitionBean : caixas.values()) {
            sum += caixaDefinitionBean.getQuantidade();
        }
        return sum;
    }

    @Override
    public Object getId() {
        return taskNodeKey;
    }

    
}
