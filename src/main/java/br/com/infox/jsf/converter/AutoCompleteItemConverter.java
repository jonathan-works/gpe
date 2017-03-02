package br.com.infox.jsf.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.autocomplete.AutoComplete;

@FacesConverter("br.com.infox.jsf.converter.autocomplete")
public class AutoCompleteItemConverter implements Converter {
    
    private List<Object> list;
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        list = loadSuggestionsFromViewMap(context, component);
        try {
            return list.get(Integer.valueOf(value));
        } catch ( NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if ( list == null ) {
            list = setSuggestionsToViewMap(context, component);
        }
        return String.valueOf(list.indexOf(value));
    }

    @SuppressWarnings("unchecked")
    public List<Object> setSuggestionsToViewMap(FacesContext context, UIComponent component) {
        List<Object> list = ((AutoComplete) component).getSuggestions();
        context.getViewRoot().getViewMap().put(component.getClientId() + "_suggestions", list);
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> loadSuggestionsFromViewMap(FacesContext context, UIComponent component) {
        return (List<Object>) context.getViewRoot().getViewMap().get(component.getClientId() + "_suggestions");
    }
    
}
