package br.com.infox.epp.access.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;

@Name(PerfilTemplateCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PerfilTemplateCrudAction extends AbstractCrudAction<PerfilTemplate, PerfilTemplateManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateCrudAction";

}
