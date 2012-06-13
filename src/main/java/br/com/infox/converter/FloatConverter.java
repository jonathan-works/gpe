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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Strings;


@org.jboss.seam.annotations.faces.Converter
@Name("floatConverter")
@BypassInterceptors
public class FloatConverter implements Converter {
	
	private static final NumberFormat formatter = new DecimalFormat("#,##0.00");

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {
		if (Strings.isEmpty(value)) {
			return null;
		}
		Double valor = null;
		try {
			valor = formatter.parse(value).doubleValue();
		} catch (Exception e) {
			throw new ConverterException(new FacesMessage("Formato inv�lido: " + value), e);
		}
		return valor;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
		return value == null ? null : formatter.format(value);
	}
	
}