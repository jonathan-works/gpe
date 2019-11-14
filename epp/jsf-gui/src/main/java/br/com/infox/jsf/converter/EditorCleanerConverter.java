package br.com.infox.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "editorCleanerConverter")
public class EditorCleanerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            return replaceAll(value);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : value.toString();
    }
    
    public static String replaceAll(String value) {
        String xml10pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
        
        return value.replaceAll(xml10pattern, "");
    }

}
