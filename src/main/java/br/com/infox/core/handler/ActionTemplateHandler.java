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
package br.com.infox.core.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionTemplate;


@Name("actionTemplateHandler")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class ActionTemplateHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ActionTemplateHandler.class);
	
	public static final String SET_CURRENT_TEMPLATE_EVENT = "setCurrentTemplateEvent";
	private List<ActionTemplate> templateList;
	private List<ActionTemplate> publicTemplateList;
	private ActionTemplate emptyExpressionTemplate;
	
	public List<ActionTemplate> getTemplateList() {
		if (templateList == null || publicTemplateList == null) {
			templateList = new ArrayList<ActionTemplate>();
			publicTemplateList = new ArrayList<ActionTemplate>();
			for (Class<? extends ActionTemplate> c : ActionTemplate.templates) {
				try {
					ActionTemplate template = c.newInstance();
					if (template.getExpression() == null) {
						emptyExpressionTemplate = template;
					} else {
						templateList.add(template);
						if (template.isPublic()) {
							publicTemplateList.add(template);
						}
					}
				} catch (Exception e) {
				    LOG.error(".getTemplateList()", e);
				}
			}
		}
		return templateList;
	}
	
	public List<ActionTemplate> getPublicTemplateList() {
		if (publicTemplateList == null) {
			getTemplateList();
		}
		return publicTemplateList;
	}
	
	public void setCurrentActionTemplate(String expression) {
		Events.instance().raiseEvent(SET_CURRENT_TEMPLATE_EVENT);
		for (ActionTemplate act : getTemplateList()) {
			String exp = "#{" + act.getExpression();
			if (expression.startsWith(exp)) {
				Contexts.getConversationContext().set("actionTemplate", act);
				act.extractParameters(expression);
				return;
			}
		}
		Contexts.getConversationContext().set("actionTemplate", 
				getEmptyExpressionTemplate());
		return;
	}

	public void setCurrentTemplate(ActionTemplate template) {
		Contexts.getConversationContext().set("actionTemplate", template);
		template.extractParameters(null);
	}
	
	public static ActionTemplateHandler instance() {
		return (ActionTemplateHandler) Component.getInstance("actionTemplateHandler");
	}

	private ActionTemplate getEmptyExpressionTemplate() {
		getTemplateList();
		return emptyExpressionTemplate;
	}
	
}