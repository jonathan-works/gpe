package br.com.infox.epp.access.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.EstruturaLocalizacoesPerfilTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;

@Named
@ViewScoped
public class PerfilTemplateCrudAction extends AbstractCrudAction<PerfilTemplate, PerfilTemplateManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PerfilTemplateManager perfilTemplateManager;

    @Override
    public void newInstance() {
        super.newInstance();
        clearTrees();
    }

    private void clearTrees() {
        BeanManager.INSTANCE.getReference(PapelTreeHandler.class).clearTree();
        BeanManager.INSTANCE.getReference(EstruturaLocalizacoesPerfilTreeHandler.class).clearTree();
    }

    @Override
    protected PerfilTemplateManager getManager() {
        return perfilTemplateManager;
    }
}
