package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;

@Name(LocalizacaoFisicaCrudAction.NAME)
public class LocalizacaoFisicaCrudAction extends AbstractCrudAction<LocalizacaoFisica> {
    
    public static final String NAME = "localizacaoFisicaCrudAction";
    
    public String inactive(LocalizacaoFisica localizacaoFisica) {
        return inactiveRecursive(localizacaoFisica);
    }
    
    @Override
    protected boolean beforeSave() {
        if (getInstance().getLocalizacaoFisicaPai() != null && !getInstance().getLocalizacaoFisicaPai().getAtivo()){
            getInstance().setAtivo(false);
        }
        return true;
    }
    
    @Override
    public String save() {
        if (!getInstance().getAtivo()){
            inactiveRecursive(getInstance());
        }
        return super.save();
    }
    
}
