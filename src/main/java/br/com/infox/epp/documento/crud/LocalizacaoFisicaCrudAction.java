package br.com.infox.epp.documento.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.infox.epp.documento.manager.LocalizacaoFisicaManager;

@Named
@ViewScoped
public class LocalizacaoFisicaCrudAction extends AbstractRecursiveCrudAction<LocalizacaoFisica, LocalizacaoFisicaManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private LocalizacaoFisicaManager localizacaoFisicaManager;

    public String inactive(LocalizacaoFisica localizacaoFisica) {
        inactiveRecursive(localizacaoFisica);
        return super.inactive(localizacaoFisica);
    }

    @Override
    protected boolean isInstanceValid() {
        if (getInstance().getLocalizacaoFisicaPai() != null
                && !getInstance().getLocalizacaoFisicaPai().getAtivo()) {
            getInstance().setAtivo(false);
        }
        return super.isInstanceValid();
    }

    @Override
    public String save() {
        if (!getInstance().getAtivo()) {
            inactiveRecursive(getInstance());
        }
        return super.save();
    }

    @Override
    public void newInstance() {
        super.newInstance();
        limparTrees();
    }

    protected void limparTrees() {
        BeanManager.INSTANCE.getReference(LocalizacaoFisicaTreeHandler.class).clearTree();
    }

    @Override
    protected LocalizacaoFisicaManager getManager() {
        return localizacaoFisicaManager;
    }
}
