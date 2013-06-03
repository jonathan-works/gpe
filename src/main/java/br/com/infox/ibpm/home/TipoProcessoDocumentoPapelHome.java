package br.com.infox.ibpm.home;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.access.entity.Papel;
import br.com.infox.ibpm.entity.TipoProcessoDocumentoPapel;

@Name("tipoProcessoDocumentoPapelHome")
public class TipoProcessoDocumentoPapelHome extends AbstractTipoProcessoDocumentoPapelHome<TipoProcessoDocumentoPapel> {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String persist() {
		instance.setTipoProcessoDocumento(TipoProcessoDocumentoHome.instance().getInstance());
		String ret = super.persist();
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
		String ejbql = "select o from Papel o";
		List<Papel> papeis = getEntityManager().createQuery(ejbql).getResultList();
		for (Iterator<Papel> iterator = papeis.iterator(); iterator.hasNext();) {
			Papel papel = iterator.next();
			if (papel.getIdentificador().startsWith("/")) {
				iterator.remove();
			}
		}
		return papeis;
	}
}