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
package br.com.itx.converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Conversions.FlatPropertyValue;
import org.jboss.seam.util.Conversions.PropertyValue;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.itx.component.Template;

@Name("templateConverter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class TemplateConverter implements Conversions.Converter<Template> {

	@Logger
	private Log log;

	public Template toObject(PropertyValue value, Type type) {
		Template t = new Template();
		String id = value.getSingleValue();
		int i = id.indexOf(':');
		if (i != -1) {
			String params = id.substring(i + 1);
			id = id.substring(0, i).trim();
			setParams(t, params);
		}
		t.setId(id);
		return t;
	}

	@SuppressWarnings("unchecked")
	private void setParams(Template t, String params) {
		try {
			t.setProperties(new HashMap<String, Object>());
			JSONObject obj = new JSONObject(params);
			for (Iterator it = obj.keys(); it.hasNext();) {
				String key = (String) it.next();
				t.getProperties().put(key, obj.get(key));
			}
		} catch (JSONException e) {
			log.warn(e);
		}
	}

	public static void main(String[] args) {
		TemplateConverter gcc = new TemplateConverter();
		String value = "texto: { " + "label : Texto, " + "type: text, " + "}";
		FlatPropertyValue p = new Conversions.FlatPropertyValue(value);
		Template template = gcc.toObject(p, null);
		System.out.println(template);
	}

}