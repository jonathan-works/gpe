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

import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Strings;


@org.jboss.seam.annotations.faces.Converter
@Name("monetarioConverter")
@Install(precedence=Install.FRAMEWORK)
@BypassInterceptors
public class MonetarioConverter implements Converter {
	
	private static final NumberFormat FORMATTER;
	private static final String SYMBOL;
	
	static {
		 FORMATTER = NumberFormat.getCurrencyInstance();
		 SYMBOL = FORMATTER.getCurrency().getSymbol();
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
			throw new ConverterException(new FacesMessage("Formato inv�lido: " + newValue), e);
		}
		return valor;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value == null ? null : FORMATTER.format(value);
	}
	
}