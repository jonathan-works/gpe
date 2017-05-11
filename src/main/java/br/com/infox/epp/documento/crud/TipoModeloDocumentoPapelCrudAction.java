package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoPapelManager;

@Name(TipoModeloDocumentoPapelCrudAction.NAME)
public class TipoModeloDocumentoPapelCrudAction extends AbstractCrudAction<TipoModeloDocumentoPapel, TipoModeloDocumentoPapelManager> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "tipoModeloDocumentoPapelCrudAction";

    private PapelManager papelManager = BeanManager.INSTANCE.getReference(PapelManager.class);

    private TipoModeloDocumento tipoModeloDocumentoAtual;

    public TipoModeloDocumento getTipoModeloDocumentoAtual() {
        return tipoModeloDocumentoAtual;
    }

    public void setTipoModeloDocumentoAtual(
            TipoModeloDocumento tipoModeloDocumentoAtual) {
        this.tipoModeloDocumentoAtual = tipoModeloDocumentoAtual;
    }

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumentoAtual() {
        return papelManager.getPapeisNaoAssociadosATipoModeloDocumento(tipoModeloDocumentoAtual);
    }

    @Override
    protected void beforeSave() {
        getInstance().setTipoModeloDocumento(tipoModeloDocumentoAtual);
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
    }

}
