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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.itx.util.StringUtil;

@org.jboss.seam.annotations.faces.Converter
@Name("cpfConverter")
@BypassInterceptors
public class CpfConverter implements Converter {
	
	private static final int TAMANHO_CPF = 11;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		String cpf = value.replaceAll("[-,\\.]", "").trim();
		return cpf.isEmpty() ? null : cpf; 
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		String ret = (String) value;
		if (ret == null || ret.length() != TAMANHO_CPF) { 
			return "";
		}
		return StringUtil.formartCpf(ret);
	}

}