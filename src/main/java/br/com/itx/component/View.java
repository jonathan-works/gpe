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

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Expressions;

@Scope(ScopeType.SESSION)
public class View implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FormField> fields;

	private String formId;

	private String home;

	private Template template;

	private Template buttons;
	
	private String showReferences;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
		if (fields != null) {
			for (FormField field : fields) {
				field.setFormId(formId);
				field.setFormHome(home);
			}
		}
	}
	
	public void setFields(List<FormField> fieldList) {
		this.fields = fieldList;
		if (formId != null) {
			for (FormField field : fields) {
				field.setFormId(formId);
				field.setFormHome(home);
			}
		}
	}

	public List<FormField> getFields() {
		return fields;
	}

	public Object getHome() {
		if (home == null) {
			home = formId + "Home";
		}
		return Component.getInstance(home, true);
	}

	public void setHome(String home) {
		this.home = home;
		if (fields != null) {
			for (FormField field : fields) {
				field.setFormHome(home);
			}
		}
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

	public boolean isShowReferences() {
		if (showReferences == null) {
			return true;
		}
		return Expressions.instance().createValueExpression(showReferences, Boolean.TYPE).getValue();
	}
	
	public void setShowReferences(String showReferences) {
		showReferences = "#{" + showReferences + "}";
		this.showReferences = showReferences;
	}
	
}
