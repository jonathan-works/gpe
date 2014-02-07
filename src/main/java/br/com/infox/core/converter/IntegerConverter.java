package br.com.infox.core.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Strings;

@org.jboss.seam.annotations.faces.Converter
@Name("integerConverter")
@BypassInterceptors
public class IntegerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (Strings.isEmpty(value)) {
            return null;
        }
        Integer valor = null;
        try {
            valor = Integer.parseInt(value);
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage("Formato inválido: "
                    + value), e);
        }
        return valor;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        return value == null ? null : value.toString();
    }

}
