package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Name(VariavelTipoModeloDocumentoCrudAction.NAME)
public class VariavelTipoModeloDocumentoCrudAction extends
		AbstractCrudAction<VariavelTipoModelo> {
	
	public static final String NAME = "variavelTipoModeloDocumentoCrudAction";
	
	private Variavel variavelAtual;

	public Variavel getVariavelAtual() {
		return variavelAtual;
	}

	public void setVariavelAtual(Variavel variavelAtual) {
		this.variavelAtual = variavelAtual;
	}
	
	@Override
	protected boolean beforeSave() {
		getInstance().setVariavel(variavelAtual);
		return super.beforeSave();
	}
	
	@Override
	protected void afterSave() {
		newInstance();
		super.afterSave();
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
