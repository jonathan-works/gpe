package br.com.infox.ibpm.home;

import br.com.infox.ibpm.entity.TipoProcessoDocumentoPapel;
import br.com.itx.component.AbstractHome;

public abstract class AbstractTipoProcessoDocumentoPapelHome<T> extends AbstractHome<TipoProcessoDocumentoPapel> {
	private static final long serialVersionUID = 1L;
	
	public void setTipoProcessoDocumentoPessoaIdTipoProcessoDocumentoPapel(Integer id) {
		setId(id);
	}
	
	public Integer getTipoProcessoDocumentoPessoaIdTipoProcessoDocumentoPapel() {
		return (Integer) getId();
	}
	
	@Override
	public String remove(TipoProcessoDocumentoPapel obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}
	
	@Override
	public String persist() {
		String ret = super.persist();
		newInstance();
		return ret;
	}
}