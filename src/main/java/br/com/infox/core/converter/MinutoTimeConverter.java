package br.com.infox.core.converter;

import java.sql.Time;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Converter
@Name("minutoTimeConverter")
@BypassInterceptors
public class MinutoTimeConverter implements Converter {

    private static final int MINUTOS_HORA = 60;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        String msgErro = "Hora inválida";
        Time horaFinal = null;
        try {
            int hora = Integer.parseInt(value) / MINUTOS_HORA;
            int minuto = Integer.parseInt(value) % MINUTOS_HORA;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, minuto);
            horaFinal = new Time(calendar.getTimeInMillis());

        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(msgErro), e);
        }
        return horaFinal;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        long minutos = 0;
        if (value instanceof Time) {
            Time time = (Time) value;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            minutos = (calendar.get(Calendar.HOUR_OF_DAY) * MINUTOS_HORA)
                    + calendar.get(Calendar.MINUTE);
        }
        return Long.toString(minutos);
    }

}
