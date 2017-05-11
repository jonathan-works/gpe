package br.com.infox.epp.access.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.EstruturaLocalizacoesPerfilTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.cdi.config.BeanManager;

@Name(PerfilTemplateCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PerfilTemplateCrudAction extends AbstractCrudAction<PerfilTemplate, PerfilTemplateManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateCrudAction";
    
    @Override
    public void newInstance() {
        super.newInstance();
        clearTrees();
    }

    @SuppressWarnings(UNCHECKED)
    private void clearTrees() {
        ((TreeHandler<Papel>) BeanManager.INSTANCE.getReference(PapelTreeHandler.class)).clearTree();
        ((TreeHandler<Localizacao>) Component.getInstance(EstruturaLocalizacoesPerfilTreeHandler.NAME)).clearTree();
    }
    
}
