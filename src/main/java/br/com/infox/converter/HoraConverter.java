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
@Name("horaConverter")
@BypassInterceptors
public class HoraConverter implements Converter {
	
	private static final int MIN_HORA = 0;
	private static final int MAX_HORA = 23;
	
	private static final int MIN_MINUTO = 0;
	private static final int MAX_MINUTO = 59;
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		String msgErro = "Hora inv�lida";
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

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
		return value.toString().substring(0, 5);
	}

}