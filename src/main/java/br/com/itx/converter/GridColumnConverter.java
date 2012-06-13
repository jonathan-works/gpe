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
import org.jboss.seam.util.Conversions.FlatPropertyValue;
import org.jboss.seam.util.Conversions.PropertyValue;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.itx.component.grid.GridColumn;

@Name("gridColumnConverter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class GridColumnConverter implements Conversions.Converter<GridColumn>{

	@Logger
	private Log log; 
	
	public GridColumn toObject(PropertyValue value, Type type) {
		GridColumn gc = new GridColumn();
		String id = value.getSingleValue();
		int i = id.indexOf(':'); 
		if (i != -1) {
			String params = id.substring(i + 1);
			id = id.substring(0, i).trim();
			setParams(gc, params);
		}
		gc.setId(id);
		return gc;
	}

	@SuppressWarnings("unchecked")
	private void setParams(GridColumn gc, String params) {
		try {
			JSONObject obj = new JSONObject(params);
			for (Iterator it = obj.keys(); it.hasNext();) {
				String key = (String) it.next();
				if (key.equals("properties")) {
					gc.setProperties(obj.getMap(key));
				} else if (key.equals("headerType")) {
					gc.setHeaderType(obj.getString(key));
				} else if (key.equals("valueType")) {
					gc.setValueType(obj.getString(key));
				} else if (key.equals("valueExpression")) {
					gc.setValueExpression(obj.getString(key));
				}
			}
		} catch (JSONException e) {
			log.warn(e);
		}
	}
	
	public static void main(String[] args) {
		GridColumnConverter gcc = new GridColumnConverter();
		StringBuilder sb = new StringBuilder();
		sb.append("texto: { ");
		sb.append("valueType : text, ");
		sb.append("headerType: textHeader, ");
		sb.append("properties: {");
		sb.append("	rendered  : false,");
		sb.append("	styleClass: msgText");
		sb.append("}");
		sb.append("}");
		FlatPropertyValue p = new Conversions.FlatPropertyValue(sb.toString());
		GridColumn column = gcc.toObject(p, null);
		System.out.println(column);
	}

}