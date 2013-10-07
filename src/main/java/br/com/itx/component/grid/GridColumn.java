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
package br.com.itx.component.grid;

import java.io.Serializable;
import java.util.Map;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.international.Messages;

public class GridColumn implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;

	private String gridId;

	private String headerType;

	private String valueType;

	private String valueExpression;

	private Map<String, String> properties;
	
	private boolean treeMode;
	
	public GridColumn() {
	}
	
	public GridColumn(String id, String gridId, String headerType, 
			          String valueType, String valueExpression, Map<String, String> properties){
		this.id = id;
		this.gridId = gridId;
		this.headerType = headerType;
		this.valueType = valueType;
		this.valueExpression = valueExpression;
		this.properties = properties;
	}
	
	public String getHeader() {
		String key = id;
		if (key.indexOf('.') > -1) {
			key = key.substring(0, key.indexOf('.'));
		}
		key = gridId + "." + key;

		Map<String, String> msg = Messages.instance();
		if (msg != null && msg.containsKey(key)) {
			return msg.get(key);
		} else {
			return id;
		}
	}

	public String getHeaderType() {
		return headerType == null ? "default" : headerType;
	}

	public void setHeaderType(String headerType) {
		this.headerType = headerType;
	}
	
	public String getValueType() {
		return valueType == null ? "default" : valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getValueExpression() {
		return valueExpression;
	}

	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}

	public Object getValue() {
		Expressions exp = Expressions.instance(); 
		return exp.createValueExpression(getVEString()).getValue();
	}

	public void setValue(Object obj) {
		Expressions exp = Expressions.instance(); 
		exp.createValueExpression(getVEString()).setValue(obj);
	}
	
	private String getVEString() {
		if (valueExpression == null) {
			if (treeMode) {
				return "#{row.entity." + id + "}";				
			} else {
				return "#{row." + id + "}";
			}
		} else if (valueExpression.startsWith("\\#{")) {
			return valueExpression.substring(1);
		} else {
			return "#{" + valueExpression + "}";
		}
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	@Override
	public String toString() {
		return gridId + "." + id;
	}
	
	public void setTreeMode(boolean treeMode) {
		this.treeMode = treeMode;
	}
}