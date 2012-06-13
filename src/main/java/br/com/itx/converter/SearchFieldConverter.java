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
import java.util.Iterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Conversions.PropertyValue;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.itx.component.grid.SearchField;

@Name("searchFieldConverter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class SearchFieldConverter implements Conversions.Converter<SearchField>{

	@Logger
	private Log log; 
	
	public SearchField toObject(PropertyValue value, Type type) {
		SearchField ff = new SearchField();
		String id = value.getSingleValue();
		int i = id.indexOf(':'); 
		if (i != -1) {
			String params = id.substring(i + 1);
			id = id.substring(0, i).trim();
			setParams(ff, params);
		}
		ff.setId(id);
		return ff;
	}

	@SuppressWarnings("unchecked")
	private void setParams(SearchField ff, String params) {
		try {
			JSONObject obj = new JSONObject(params);
			for (Iterator it = obj.keys(); it.hasNext();) {
				String key = (String) it.next();
				if (key.equals("properties")) {
					ff.setProperties(obj.getMap(key));
				} else if (key.equals("label")) {
					ff.setLabel(obj.getString(key));
				} else if (key.equals("type")) {
					ff.setType(obj.getString(key));
				} else if (key.equals("required")) {
					ff.setRequired(obj.getBoolean(key));
				}
			}
		} catch (JSONException e) {
			log.warn(e);
		}
	}
	
}