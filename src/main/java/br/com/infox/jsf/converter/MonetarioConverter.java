package br.com.infox.jsf.converter;

import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.util.Strings;

@FacesConverter("monetarioConverter")
public class MonetarioConverter implements Converter {

    private static final NumberFormat FORMATTER;
    private static final String SYMBOL;

    static {
        FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        SYMBOL = FORMATTER.getCurrency().getSymbol(new Locale("pt", "BR"));
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (Strings.isEmpty(value)) {
            return null;
        }
        String newValue = value;
        if (!value.startsWith(SYMBOL)) {
            newValue = SYMBOL + " " + value;
        }
        Double valor = null;
        try {
            valor = FORMATTER.parse(newValue).doubleValue();
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage("Formato inválido: " + newValue), e);
        }
        return valor;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : FORMATTER.format(value);
    }

}
