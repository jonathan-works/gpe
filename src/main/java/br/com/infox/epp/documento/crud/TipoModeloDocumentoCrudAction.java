package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;

@Name(TipoModeloDocumentoCrudAction.NAME)
public class TipoModeloDocumentoCrudAction extends AbstractCrudAction<TipoModeloDocumento, TipoModeloDocumentoManager> {
    
    private static final long serialVersionUID = 1L;

    public static final String NAME = "tipoModeloDocumentoCrudAction";
	
	@In private ModeloDocumentoManager modeloDocumentoManager;
	
	public List<ModeloDocumento> getListaDeModeloDocumento(){
		return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(getInstance().getGrupoModeloDocumento(), getInstance());
	}
	
}