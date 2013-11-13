package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Name(TipoModeloDocumentoVariavelCrudAction.NAME)
public class TipoModeloDocumentoVariavelCrudAction extends
		AbstractCrudAction<VariavelTipoModelo> {
	
	public static final String NAME = "tipoModeloDocumentoVariavelCrudAction";
	
	private TipoModeloDocumento tipoModeloDocumentoAtual;

	public TipoModeloDocumento getTipoModeloDocumentoAtual() {
		return tipoModeloDocumentoAtual;
	}

	public void setTipoModeloDocumentoAtual(TipoModeloDocumento tipoModeloDocumentoAtual) {
		this.tipoModeloDocumentoAtual = tipoModeloDocumentoAtual;
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
	
	public void addVariavelTipoModelo(Variavel obj) {
		getInstance().setVariavel(obj);
		save();
		FacesMessages.instance().clear();
	}
	
	public void removeVariavelTipoModelo(VariavelTipoModelo obj) {
		remove(obj);
	}
	
}
