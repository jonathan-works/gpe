package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.manager.VariavelTipoModeloManager;

@Name(TipoModeloDocumentoVariavelCrudAction.NAME)
public class TipoModeloDocumentoVariavelCrudAction extends AbstractCrudAction<VariavelTipoModelo, VariavelTipoModeloManager> {
	
    private static final long serialVersionUID = 1L;

    public static final String NAME = "tipoModeloDocumentoVariavelCrudAction";
	
	private TipoModeloDocumento tipoModeloDocumentoAtual;

	public TipoModeloDocumento getTipoModeloDocumentoAtual() {
		return tipoModeloDocumentoAtual;
	}

	public void setTipoModeloDocumentoAtual(TipoModeloDocumento tipoModeloDocumentoAtual) {
		this.tipoModeloDocumentoAtual = tipoModeloDocumentoAtual;
	}
	
	@Override
	protected boolean isInstanceValid() {
		getInstance().setTipoModeloDocumento(tipoModeloDocumentoAtual);
		return super.isInstanceValid();
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
