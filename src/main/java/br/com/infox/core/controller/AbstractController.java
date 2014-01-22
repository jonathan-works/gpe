package br.com.infox.core.controller;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractController implements Controller {
    
    private String tab;
    private Object id;

    @Override
    public String getTab() {
        return tab;
    }

    @Override
    public void setTab(String tab) {
        this.tab = tab;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }
    
}
