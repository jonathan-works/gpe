package br.com.infox.epp.ajuda.view;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(AjudaView.NAME)
@Scope(ScopeType.PAGE)
public class AjudaView <Ajuda>{
    
    public static final String NAME = "ajudaView";
    
    private String tab;
    
    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

}
