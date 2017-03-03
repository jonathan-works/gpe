package br.com.infox.jsf.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("br.com.infox.jsf.converter.autocomplete")
public class AutoCompleteItemConverter implements Converter {
    
    private List<Object> list;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if ( list == null ) list = loadSuggestionsFromViewMap(context, component);
        try {
            return list.get(Integer.valueOf(value));
        } catch ( NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (isNewQuery(context, component) && list == null) {
            clearSuggestionsViewMap(context, component);
        }
        addSuggestionToViewMap(context, component, value);
        return String.valueOf(list.indexOf(value));
    }

    private boolean isNewQuery(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String query = params.get(component.getClientId(context) + "_query");
        return query != null;
    }

    public void clearSuggestionsViewMap(FacesContext context, UIComponent component) {
        context.getViewRoot().getViewMap().put(component.getClientId() + "_suggestions", null);
    }
    
    private void addSuggestionToViewMap(FacesContext context, UIComponent component, Object value) {
        if ( list == null ) { 
            list = new ArrayList<Object>();
            context.getViewRoot().getViewMap().put(component.getClientId() + "_suggestions", list);
        }
        list.add(value);
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> loadSuggestionsFromViewMap(FacesContext context, UIComponent component) {
        return (List<Object>) context.getViewRoot().getViewMap().get(component.getClientId() + "_suggestions");
    }
    
}
