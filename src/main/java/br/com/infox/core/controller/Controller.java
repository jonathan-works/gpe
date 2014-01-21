package br.com.infox.core.controller;

public interface Controller {
    
    String TAB_FORM = "form";
    String TAB_SEARCH = "search";
    
    String getTab();
    void setTab(String tab);
    
    Object getId();
    void setId(Object id);
    
    public void onClickSearchTab();
    public void onClickFormTab();

}
