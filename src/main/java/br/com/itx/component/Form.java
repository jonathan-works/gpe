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
package br.com.itx.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.SESSION)
public class Form implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FormField> fields = new ArrayList<FormField>();

	private List<FormField> rootFields = new ArrayList<FormField>();
	
	private Map<String, List<FormField>> tabbedFields = new HashMap<String, List<FormField>>();

	private String formId;

	private String home;

	private Template template;

	private Template buttons;

	public void setFields(List<FormField> fieldList) {
		tabbedFields.clear();
		rootFields.clear();
		this.fields = fieldList;
		String currentTab = null;
		for (FormField field : fields) {
			if (field.getType().equals("tab")) {
				currentTab = field.getId();
				rootFields.add(field);
			} else if (currentTab == null) {
				rootFields.add(field);
			} else {
				List<FormField> tabFields = tabbedFields.get(currentTab);
				if (tabFields == null) {
					tabFields = new ArrayList<FormField>();
					tabbedFields.put(currentTab, tabFields);
				}
				tabFields.add(field);
			}
			field.setFormId(formId);
			field.setFormHome(home);
		}
	}

	public List<FormField> getFields() {
		return fields;
	}	

	public List<FormField> getRootFields() {
		if (isTabbed()) {
			return rootFields;
		} else {
			return getFields();
		}
	}
	
	public List<FormField> getTabFields(String tabId) {
		return tabbedFields.get(tabId);
	}
	
	public boolean isTabbed() {
		return !tabbedFields.isEmpty();
	}
	
	public Object getHome() {
		return Component.getInstance(getHomeName(), true);
	}

	public void setHome(String home) {
		this.home = home;
	}
	
	public String getHomeName() {
		if (home == null) {
			home = formId + "Home";
		}		
		return home;
	}

	public Template getButtons() {
		if (buttons == null) {
			buttons = new Template();
		}
		return buttons;
	}

	public void setButtons(Template buttons) {
		this.buttons = buttons;
	}

	public Template getTemplate() {
		if (template == null) {
			template = new Template();
		}
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
		for (FormField field : fields) {
			field.setFormId(formId);
			field.setFormHome(home);
		}
	}

	public boolean hasRequiredField() {
		for(FormField ff : fields) {
			if(ff.isRequired()) {
				return true;
			}
		}
		return false;
	}
	
}