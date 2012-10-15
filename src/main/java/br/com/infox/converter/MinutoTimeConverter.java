/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.converter;

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
			String value) throws ConverterException {
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		String msgErro = "Hora inv�lida";
		Time horaFinal = null;
		try {	
			int hora = Integer.parseInt(value)/MINUTOS_HORA;
			int minuto = Integer.parseInt(value)%MINUTOS_HORA;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,hora);
			calendar.set(Calendar.MINUTE, minuto);
			horaFinal = new Time(calendar.getTimeInMillis());
			
		} catch (Exception e) {
			throw new ConverterException(new FacesMessage(msgErro), e);
		}
		return horaFinal;
	}
	

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
		long minutos = 0;
		if (value instanceof Time) {
			Time time = (Time) value;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			minutos = (calendar.get(Calendar.HOUR_OF_DAY) * MINUTOS_HORA) + 
				calendar.get(Calendar.MINUTE);
		}
		return Long.toString(minutos);
	}

}