package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoPapelDAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;

@Name(TipoProcessoDocumentoPapelManager.NAME)
@AutoCreate
public class TipoProcessoDocumentoPapelManager extends Manager<TipoProcessoDocumentoPapelDAO, TipoProcessoDocumentoPapel> {
	private static final long serialVersionUID = 4455754174682600299L;
	public static final String NAME = "tipoProcessoDocumentoPapelManager";
}