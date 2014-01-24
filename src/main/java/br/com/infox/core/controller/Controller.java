package br.com.infox.core.controller;

public interface Controller {
    
    String getTab();
    void setTab(String tab);
    
    Object getId();
    void setId(Object id);
    void onClickSearchTab();
    void onClickFormTab();
}
