package br.com.infox.jsf.converter;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.joda.time.DateTime;

@org.jboss.seam.annotations.faces.Converter
@Name("horaConverter")
@BypassInterceptors
public class HoraConverter implements Converter {
	
	private static final String HOUR_FORMAT = "%02d:%02d";
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        DateTime datetime = null;
        try {
            String[] horario = value.split(":");
            int hora = Integer.parseInt(horario[0]);
            int minuto = Integer.parseInt(horario[1]);
            datetime = new DateTime(1970, 1, 1, hora, minuto, 0, 0);
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage("Hora inválida"), e);
        }
        return datetime.toDate();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
    	if (value == null) {
    		return "";
    	} else {
    		DateTime date = new DateTime((Date) value);
    		return String.format(HOUR_FORMAT, date.getHourOfDay(), date.getMinuteOfHour());
    	}
    }

}
