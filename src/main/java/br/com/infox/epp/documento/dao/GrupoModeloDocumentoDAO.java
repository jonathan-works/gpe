package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Name(GrupoModeloDocumentoDAO.NAME)
@AutoCreate
public class GrupoModeloDocumentoDAO extends DAO<GrupoModeloDocumento> {

	public static final String NAME = "grupoModeloDocumento";
	private static final long serialVersionUID = 1L;
}
