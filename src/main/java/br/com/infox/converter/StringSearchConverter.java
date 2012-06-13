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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Converter
@Name("stringSearchConverter")
@BypassInterceptors
public class StringSearchConverter implements Converter{

	private static char[] scapes = {'%'};
	
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		StringConverter sc = new StringConverter();
		String out = (String) sc.getAsObject(context, component, value);
		if(out != null) {
			for (char scape : scapes) { 
				out = out.replace(Character.toString(scape), "\\" + Character.toString(scape));
			}
		}
		return out;
	}
	
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		return value == null ? null : value.toString();
	}
	
}