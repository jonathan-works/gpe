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
