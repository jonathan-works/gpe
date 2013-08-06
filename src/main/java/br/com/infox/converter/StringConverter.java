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
import org.jboss.seam.util.Strings;


@org.jboss.seam.annotations.faces.Converter
@Name("stringConverter")
@BypassInterceptors
public class StringConverter implements Converter {
	
	private static char[][] replaceCharTable = {
			{(char) 8211, '-'},
			{(char) 45,   '-'},
			{(char) 8221, '"'},
			{(char) 8220, '"'},
			{(char) 28, '"'},
			{(char) 29, '"'},
			//referente ao caractere: flecha
			{(char) 8594, '-'}
	};

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		String saida = value;
		for (char[] tupla : replaceCharTable) {
			saida = saida.replace(tupla[0], tupla[1]);
		}
		return Strings.nullIfEmpty(saida.trim());
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		return value == null ? null : value.toString();
	}
	
}