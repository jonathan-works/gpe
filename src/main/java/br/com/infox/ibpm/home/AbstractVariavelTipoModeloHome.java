package br.com.infox.ibpm.home;

import br.com.infox.ibpm.entity.VariavelTipoModelo;
import br.com.itx.component.AbstractHome;

public abstract class AbstractVariavelTipoModeloHome<T> extends AbstractHome<VariavelTipoModelo> {

	private static final long serialVersionUID = 1L;

	public void setVariavelTipoModeloIdVariavelTipoModelo(Integer id) {
		setId(id);
	}

	public Integer getVariavelTipoModeloIdVariavelTipoModelo() {
		return (Integer) getId();
	}
	
	@Override
	protected VariavelTipoModelo createInstance() {
		VariavelTipoModelo variavelTipoModelo = new VariavelTipoModelo();
		
		VariavelHome variavelHome = VariavelHome.instance();
		if (variavelHome != null) {
			variavelTipoModelo.setVariavel(variavelHome.getDefinedInstance());
		}
		
		TipoModeloDocumentoHome tipoModeloDocumentoHome = TipoModeloDocumentoHome.instance();
		if (tipoModeloDocumentoHome != null) {
			variavelTipoModelo.setTipoModeloDocumento(tipoModeloDocumentoHome.getDefinedInstance());
		}		
		return variavelTipoModelo;
	}

	@Override
	public String remove() {
		VariavelHome variavel = VariavelHome.instance();
		if (variavel != null) {
			variavel.getInstance().getVariavelTipoModeloList().remove(instance);
		}
		TipoModeloDocumentoHome tipoModeloDocumento = TipoModeloDocumentoHome.instance();
		if (tipoModeloDocumento != null) {
			tipoModeloDocumento.getInstance().getVariavelTipoModeloList().remove(instance);
		}
		return super.remove();
	}

	@Override
	public String remove(VariavelTipoModelo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("variavelTipoModeloGrid");
		return ret;
	}

	@Override
    public String persist() {
       String action = super.persist();
       newInstance();
       return action;
    }

}