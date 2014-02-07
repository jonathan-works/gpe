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
@Name("horaConverter")
@BypassInterceptors
public class HoraConverter implements Converter {

    private static final int MIN_HORA = 0;
    private static final int MAX_HORA = 23;

    private static final int MIN_MINUTO = 0;
    private static final int MAX_MINUTO = 59;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        String msgErro = "Hora inválida";
        Time horaFinal = null;
        try {
            String[] horario = value.split(":");
            int hora = Integer.parseInt(horario[0]);
            int minuto = Integer.parseInt(horario[1]);
            if (hora > MAX_HORA || hora < MIN_HORA) {
                msgErro = "Selecionar hora entre 0 e 23";
                throw new ConverterException(new FacesMessage(msgErro));
            }
            if (minuto > MAX_MINUTO || minuto < MIN_MINUTO) {
                msgErro = "Selecionar minuto entre 0 e 59";
                throw new ConverterException(new FacesMessage(msgErro));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 1970);
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, minuto);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            horaFinal = new Time(calendar.getTimeInMillis());
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(msgErro), e);
        }
        return horaFinal;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        return value.toString().substring(0, 5);
    }

}
