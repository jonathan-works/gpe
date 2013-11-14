package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Name(TipoModeloDocumentoPapelCrudAction.NAME)
public class TipoModeloDocumentoPapelCrudAction 
				extends AbstractCrudAction<TipoModeloDocumentoPapel> {
	
	public static final String NAME = "tipoModeloDocumentoPapelCrudAction";
	
	@In private PapelManager papelManager;
	
	private TipoModeloDocumento tipoModeloDocumentoAtual;

	public TipoModeloDocumento getTipoModeloDocumentoAtual() {
		return tipoModeloDocumentoAtual;
	}

	public void setTipoModeloDocumentoAtual(TipoModeloDocumento tipoModeloDocumentoAtual) {
		this.tipoModeloDocumentoAtual = tipoModeloDocumentoAtual;
	}
	
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumentoAtual(){
		return papelManager.getPapeisNaoAssociadosATipoModeloDocumento(tipoModeloDocumentoAtual);
	}
	
	@Override
	protected boolean beforeSave() {
		getInstance().setTipoModeloDocumento(tipoModeloDocumentoAtual);
		return super.beforeSave();
	}
	
	@Override
	protected void afterSave() {
		newInstance();
		super.afterSave();
	}
	
}
