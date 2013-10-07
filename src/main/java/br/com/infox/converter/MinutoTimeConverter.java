/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
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
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		String msgErro = "Hora inválida";
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
	public String getAsString(FacesContext context, UIComponent component, Object value) {
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