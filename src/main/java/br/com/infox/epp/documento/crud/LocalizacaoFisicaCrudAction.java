package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.documento.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.itx.util.ComponentUtil;

@Name(LocalizacaoFisicaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoFisicaCrudAction extends AbstractRecursiveCrudAction<LocalizacaoFisica> {
    
    public static final String NAME = "localizacaoFisicaCrudAction";
    
    public String inactive(LocalizacaoFisica localizacaoFisica) {
        inactiveRecursive(localizacaoFisica);
        return super.inactive(localizacaoFisica);
    }
    
    @Override
    protected boolean beforeSave() {
        if (getInstance().getLocalizacaoFisicaPai() != null && !getInstance().getLocalizacaoFisicaPai().getAtivo()){
            getInstance().setAtivo(false);
        }
        return super.beforeSave();
    }
    
    @Override
    public String save() {
        if (!getInstance().getAtivo()){
            inactiveRecursive(getInstance());
        }
        return super.save();
    }
    
    @Override
    protected void afterSave() {
        limparTrees();
        super.afterSave();
    }
    
    @Override
    protected void limparTrees(){
        LocalizacaoFisicaTreeHandler lfth = ComponentUtil.getComponent(LocalizacaoFisicaTreeHandler.NAME);
        lfth.clearTree();
    }
    
}
