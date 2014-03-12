package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.manager.VariavelTipoModeloManager;

@Name(VariavelTipoModeloDocumentoCrudAction.NAME)
public class VariavelTipoModeloDocumentoCrudAction extends AbstractCrudAction<VariavelTipoModelo, VariavelTipoModeloManager> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "variavelTipoModeloDocumentoCrudAction";

    private Variavel variavelAtual;

    public Variavel getVariavelAtual() {
        return variavelAtual;
    }

    public void setVariavelAtual(Variavel variavelAtual) {
        this.variavelAtual = variavelAtual;
    }

    @Override
    protected void beforeSave() {
        getInstance().setVariavel(variavelAtual);
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
    }

    public void addTipoModeloVariavel(TipoModeloDocumento obj) {
        getInstance().setTipoModeloDocumento(obj);
        save();
        FacesMessages.instance().clear();
    }

    public void removeTipoModeloVariavel(VariavelTipoModelo obj) {
        remove(obj);
    }
}
