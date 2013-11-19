package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;

@Name(LocalizacaoTipoModeloDocumentoCrudAction.NAME)
public class LocalizacaoTipoModeloDocumentoCrudAction extends AbstractCrudAction<ItemTipoDocumento> {
    
    public static final String NAME = "localizacaoTipoModeloDocumentoCrudAction";
    
    private Localizacao localizacaoAtual;

    public Localizacao getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Localizacao localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }
    
    @Override
    public String save() {
        getInstance().setLocalizacao(localizacaoAtual);
        return super.save();
    }
    
    @Override
    protected void afterSave() {
        newInstance();
        super.afterSave();
    }
    
    

}
