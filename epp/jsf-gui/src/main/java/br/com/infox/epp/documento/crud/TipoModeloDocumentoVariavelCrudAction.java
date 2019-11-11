package br.com.infox.epp.documento.crud;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.manager.VariavelTipoModeloManager;

@Named
@ViewScoped
public class TipoModeloDocumentoVariavelCrudAction extends AbstractCrudAction<VariavelTipoModelo, VariavelTipoModeloManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private VariavelTipoModeloManager variavelTipoModeloManager;

    private TipoModeloDocumento tipoModeloDocumentoAtual;

    public TipoModeloDocumento getTipoModeloDocumentoAtual() {
        return tipoModeloDocumentoAtual;
    }

    public void setTipoModeloDocumentoAtual(
            TipoModeloDocumento tipoModeloDocumentoAtual) {
        this.tipoModeloDocumentoAtual = tipoModeloDocumentoAtual;
    }

    @Override
    protected void beforeSave() {
        getInstance().setTipoModeloDocumento(tipoModeloDocumentoAtual);
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
    }

    public void addVariavelTipoModelo(Variavel obj) {
        getInstance().setVariavel(obj);
        save();
        FacesMessages.instance().clear();
    }

    public void removeVariavelTipoModelo(VariavelTipoModelo obj) {
        remove(obj);
    }

    @Override
    protected VariavelTipoModeloManager getManager() {
        return variavelTipoModeloManager;
    }
}
