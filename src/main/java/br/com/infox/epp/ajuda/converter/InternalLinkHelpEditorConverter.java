package br.com.infox.epp.ajuda.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.Component;

import br.com.infox.seam.path.PathResolver;

@FacesConverter("internalLinkHelpEditorConverter")
public class InternalLinkHelpEditorConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
            String contextPath = pathResolver.getContextPath();
            int originalLength = value.length();
            StringBuilder texto = new StringBuilder(value);
            Matcher matcher = Pattern.compile("((http|https)://.+?)/+?" + contextPath.substring(1)).matcher(value);
            while (matcher.find()) {
               int startIndex = matcher.start(1);
               int endIndex = matcher.end(1);
               startIndex = startIndex - (originalLength - texto.length());
               endIndex = endIndex - (originalLength - texto.length());
               texto.delete(startIndex, endIndex);
            }
            return texto.toString();
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : (String) value;
    }
}
