package br.com.infox.epp.processo.consulta.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(ConsultaExternaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ConsultaExternaAction {
    
    public static final String NAME = "consultaExternaAction";
    
    private String tab;

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}
