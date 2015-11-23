package br.com.infox.jsf.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.util.Strings;

@FacesConverter("floatConverter")
public class FloatConverter implements Converter {

    private static final NumberFormat FORMATTER = new DecimalFormat("#,##0.00");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (Strings.isEmpty(value)) {
            return null;
        }
        Double valor = null;
        try {
            valor = FORMATTER.parse(value).doubleValue();
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage("Formato inválido: " + value), e);
        }
        return valor;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : FORMATTER.format(value);
    }

}
