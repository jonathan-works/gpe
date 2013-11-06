package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.TipoModeloDocumentoPapelDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Name(TipoModeloDocumentoPapelManager.NAME)
@AutoCreate
public class TipoModeloDocumentoPapelManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoModeloDocumentoPapelManager";
	
	@In private TipoModeloDocumentoPapelDAO tipoModeloDocumentoPapelDAO;
	
	public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos(){
		return tipoModeloDocumentoPapelDAO.getTiposModeloDocumentoPermitidos();
	}
	
}
