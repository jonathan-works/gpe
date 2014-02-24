package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;
import br.com.infox.epp.documento.manager.ItemTipoDocumentoManager;

@Name(LocalizacaoTipoModeloDocumentoCrudAction.NAME)
public class LocalizacaoTipoModeloDocumentoCrudAction extends AbstractCrudAction<ItemTipoDocumento, ItemTipoDocumentoManager> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "localizacaoTipoModeloDocumentoCrudAction";
    
    private Localizacao localizacaoAtual;

    public Localizacao getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Localizacao localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }
    
    @Override
    protected void beforeSave() {
    	getInstance().setLocalizacao(localizacaoAtual);
    }
    
    @Override
    protected void afterSave(String ret) {
        newInstance();
    }
    
    

}
