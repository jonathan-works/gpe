package br.com.infox.jsf.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.util.Strings;

@FacesConverter("longConverter")
public class LongConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (Strings.isEmpty(value)) {
            return null;
        }
        Long valor = null;
        try {
            valor = Long.parseLong(value);
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage("Formato inválido: " + value), e);
        }
        return valor;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : value.toString();
    }

}
