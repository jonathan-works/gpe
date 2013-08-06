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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Strings;


@org.jboss.seam.annotations.faces.Converter
@Name("integerConverter")
@BypassInterceptors
public class IntegerConverter implements Converter {
	

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (Strings.isEmpty(value)) {
			return null;
		}
		Integer valor = null;
		try {
			valor = Integer.parseInt(value);
		} catch (Exception e) {
			throw new ConverterException(new FacesMessage("Formato inv�lido: " + value), e);
		}
		return valor;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value == null ? null : value.toString();
	}
	
}