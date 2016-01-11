package br.com.infox.epp.painel;

import java.util.List;

public interface PanelDefinition {
    
    Object getId();
    
    String getName();
    
    int getQuantidade();
    
    List<TaskBean> getTasks();
    
}
