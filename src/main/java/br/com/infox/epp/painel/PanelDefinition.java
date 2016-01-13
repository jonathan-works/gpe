package br.com.infox.epp.painel;

import java.util.List;

import br.com.infox.epp.painel.caixa.Caixa;

public interface PanelDefinition {
    
    Object getId();
    
    String getName();
    
    int getQuantidade();
    
    List<TaskBean> getTasks();
    
    void moverParaCaixa(TaskBean taskBean, Caixa caixa);
    
}
