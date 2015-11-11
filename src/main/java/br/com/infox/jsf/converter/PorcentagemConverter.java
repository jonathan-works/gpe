package br.com.infox.jsf.converter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.google.common.base.Strings;

@FacesConverter("porcentagemConverter")
public class PorcentagemConverter implements Converter{

	private static final NumberFormat FORMATTER;
    private static final String SYMBOL = "%";

    static {
        FORMATTER = DecimalFormat.getPercentInstance(new Locale("pt", "BR"));
        FORMATTER.setParseIntegerOnly(false);
        FORMATTER.setMaximumFractionDigits(2);
        FORMATTER.setRoundingMode(RoundingMode.UNNECESSARY);
    }
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(!Strings.isNullOrEmpty(value)){
			String newValue = value;
			if(!value.endsWith(SYMBOL)){
				newValue = value + SYMBOL;
			}
			try {
				return FORMATTER.parse(newValue).doubleValue();
			} catch (ParseException e) {
				throw new ConverterException(new FacesMessage("Formato inválido: "
	                    + newValue), e);
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : FORMATTER.format(value);
	}

}
