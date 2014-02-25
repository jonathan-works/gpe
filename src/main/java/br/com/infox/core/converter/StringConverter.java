package br.com.infox.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Strings;

@org.jboss.seam.annotations.faces.Converter
@Name("stringConverter")
@BypassInterceptors
public class StringConverter implements Converter {

    private static char[][] replaceCharTable = { { (char) 8211, '-' },
        { (char) 45, '-' }, { (char) 8221, '"' }, { (char) 8220, '"' },
        { (char) 28, '"' }, { (char) 29, '"' },
        // referente ao caractere: flecha
        { (char) 8594, '-' } };

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        String saida = value;
        for (char[] tupla : replaceCharTable) {
            saida = saida.replace(tupla[0], tupla[1]);
        }
        return Strings.nullIfEmpty(saida.trim());
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        return value == null ? null : value.toString();
    }

}
