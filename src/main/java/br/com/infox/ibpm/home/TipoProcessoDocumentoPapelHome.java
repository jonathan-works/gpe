package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.epp.manager.PapelManager;
import br.com.infox.ibpm.entity.TipoProcessoDocumentoPapel;

@Name(TipoProcessoDocumentoPapelHome.NAME)
public class TipoProcessoDocumentoPapelHome extends AbstractTipoProcessoDocumentoPapelHome<TipoProcessoDocumentoPapel> {
	
	public static final String NAME = "tipoProcessoDocumentoPapelHome";
	private static final long serialVersionUID = 1L;
	
	@In PapelManager papelManager;
	
	@Override
	public String persist() {
		instance.setTipoProcessoDocumento(TipoProcessoDocumentoHome.instance().getInstance());
		String ret = super.persist();
		newInstance();
		return ret;
	}
	
	@Override
	public String update() {
		instance.setTipoProcessoDocumento(TipoProcessoDocumentoHome.instance().getInstance());
		String ret = super.update();
		return ret;
	}
	
	@Override
	public String remove() {
		instance.setTipoProcessoDocumento(TipoProcessoDocumentoHome.instance().getInstance());
		newInstance();
		return super.remove();
	}
	
	public List<Papel> papelItems() {
		return papelManager.getPapeisNaoAssociadosATipoProcessoDocumento(TipoProcessoDocumentoHome.instance().getInstance());
	}
}