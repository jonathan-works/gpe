package br.com.infox.jbpm.action;

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
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name("actionTemplateHandler")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class ActionTemplateHandler implements Serializable {

    private static final String ACTION_TEMPLATE = "actionTemplate";
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ActionTemplateHandler.class);

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
        for (ActionTemplate act : getTemplateList()) {
            String exp = "#{" + act.getExpression();
            if (expression.startsWith(exp)) {
                Contexts.getPageContext().set(ACTION_TEMPLATE, act);
                act.extractParameters(expression);
                return;
            }
        }
        Contexts.getPageContext().set(ACTION_TEMPLATE, getEmptyExpressionTemplate());
        return;
    }

    public void setCurrentTemplate(ActionTemplate template) {
        Contexts.getPageContext().set(ACTION_TEMPLATE, template);
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
