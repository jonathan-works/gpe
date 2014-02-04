package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.GrupoModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Name(GrupoModeloDocumentoManager.NAME)
@AutoCreate
public class GrupoModeloDocumentoManager extends Manager<GrupoModeloDocumentoDAO, GrupoModeloDocumento> {
	private static final long serialVersionUID = 4455754174682600299L;
	public static final String NAME = "grupoModeloDocumentoManager";
}