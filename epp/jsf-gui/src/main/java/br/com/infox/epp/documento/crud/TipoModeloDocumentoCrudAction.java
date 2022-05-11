package br.com.infox.epp.documento.crud;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.Component;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.list.TipoModeloDocumentoPapelList;
import br.com.infox.epp.documento.list.associated.AssociatedVariavelTipoModeloList;
import br.com.infox.epp.documento.list.associative.AssociativeVariavelList;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;

@Named
@ViewScoped
public class TipoModeloDocumentoCrudAction extends AbstractCrudAction<TipoModeloDocumento, TipoModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    @Inject
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;

    @Inject
    private AssociatedVariavelTipoModeloList associatedVariavelTipoModeloList;
    @Inject
    private AssociativeVariavelList associativeVariavelList;
    @Inject
    private TipoModeloDocumentoVariavelCrudAction tipoModeloDocumentoVariavelCrudAction;
    @Inject
    private TipoModeloDocumentoPapelCrudAction tipoModeloDocumentoPapelCrudAction;
    @Inject
    private TipoModeloDocumentoPapelList tipoModeloDocumentoPapelList;

    public List<ModeloDocumento> getListaDeModeloDocumento() {
        final TipoModeloDocumento tipoModeloDocumento = getInstance();
        if (isManaged()) {
            return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(tipoModeloDocumento.getGrupoModeloDocumento(), tipoModeloDocumento);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected TipoModeloDocumentoManager getManager() {
        return tipoModeloDocumentoManager;
    }

    @Override
    public void newInstance() {
        setId(null);
        TipoModeloDocumento tipoModDoc = new TipoModeloDocumento();
        tipoModDoc.setNumeracaoAutomatica(Boolean.FALSE);
        setInstance(tipoModDoc);
    }

    public void onClickVariaveisTab() {
        associatedVariavelTipoModeloList.getEntity().setTipoModeloDocumento(getInstance());
        tipoModeloDocumentoVariavelCrudAction.setTipoModeloDocumentoAtual(getInstance());
        associativeVariavelList.setTipoModeloToIgnore(getInstance());
        associativeVariavelList.onClickVariaveisTab(getInstance());
    }

    public void onClickPapeisTab() {
        tipoModeloDocumentoPapelCrudAction.setTipoModeloDocumentoAtual(getInstance());
        tipoModeloDocumentoPapelList.getEntity().setTipoModeloDocumento(getInstance());
    }
    
    public void onChangeNumeracaoAutomatica() {
    	if (!Objects.isNull(getInstance()) && !Objects.isNull(getInstance().getNumeracaoAutomatica()) 
    			&& getInstance().getNumeracaoAutomatica().equals(Boolean.FALSE)) {
    		getInstance().setNumeroDocumentoInicial(null);
    		getInstance().setReiniciaNumeracaoAnual(null);
    	}
    }

    @Override
    public String getHomeName() {
        String componentName = ReflectionsUtil.getCdiComponentName(getClass());
        if (componentName == null) {
            return Component.getComponentName(this.getClass());
        }
        return componentName;
    }

}
