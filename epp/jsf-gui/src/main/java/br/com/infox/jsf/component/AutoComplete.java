package br.com.infox.jsf.component;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

public class AutoComplete extends org.primefaces.component.autocomplete.AutoComplete {
    @Override
    public String resolveWidgetVar() {
        FacesContext context = getFacesContext();
        String userWidgetVar = (String) getAttributes().get("widgetVar");

        if(userWidgetVar != null && !userWidgetVar.trim().isEmpty())
            return userWidgetVar;
         else
            return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
    }
}
