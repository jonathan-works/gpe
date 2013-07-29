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
package br.com.itx.component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.international.Messages;

import br.com.itx.component.grid.GridQuery;
import br.com.itx.component.grid.SearchField;

public class FormField implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private Form form;
	
	private String formId;
	
	private String formHome;

	private String label;

	private String type;

	private String valueExpression;
	
	private String required;

	private String rendered = "true";

	private Map<String, Object> properties = new PropertyMap<String, Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		if (label == null) {
			String key = id;
			if (key.indexOf('.') > -1) {
				key = key.substring(0, key.indexOf('.'));
			}
			key = getFormId() + "." + key;
			Map<String, String> msg = Messages.instance();
			if (msg != null && msg.containsKey(key)) {
				label =  msg.get(key);
			} else {
				label = label != null ? label : id;
			}
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type == null ? "default" : type;
	}

	public void setType(String type) {
		this.type = type;
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
			if (formHome != null) {
				return "#{" + getFormHome() + ".instance." + id + "}";
			} else {
				return "#{" + getFormId() + "Form.home.instance." + id + "}";
			}	
		} else if (valueExpression.startsWith("\\#{")) {
			return valueExpression.substring(1);
		} else {
			return "#{" + valueExpression + "}";
		}
	}

	public boolean isRequired() {
		Boolean value = Boolean.FALSE;
		try {
			Expressions exp = Expressions.instance(); 
			value = (Boolean) exp.createValueExpression("#{" + required + "}").getValue();
			if (value == null) {
				value = Boolean.FALSE;
			}
		} catch (RuntimeException e) {
		}
		return value.booleanValue();
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public boolean isRendered() {
		Boolean value = Boolean.FALSE;
		try {
			Expressions exp = Expressions.instance(); 
			value = (Boolean) exp.createValueExpression("#{" + rendered + "}").getValue();
			if (value == null) {
				value = Boolean.TRUE;
			}
		} catch (RuntimeException e) {
		}
		return value.booleanValue();
	}

	public void setRendered(String rendered) {
		this.rendered = rendered;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties.clear();
		this.properties.putAll(properties);
	}

	public String getFormId() {
		if (form != null) {
			formId = form.getFormId();
		}
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public String getFormHome() {
		return formHome;
	}

	public void setFormHome(String formHome) {
		this.formHome = formHome;
	}

	@SuppressWarnings("rawtypes")
	public List suggest(Object typed) {
		String column = (String) properties.get("optionText");
		if (column != null) {
			String gridId = (String) properties.get("source") + "Grid";
			GridQuery grid = (GridQuery) Component.getInstance(gridId);
			for (SearchField sf : grid.getSearchFields()) {
				if (sf.getId().equals(column)) {
					return sf.suggest(typed);
				}
			}
		}
		return null;
	}
	
}