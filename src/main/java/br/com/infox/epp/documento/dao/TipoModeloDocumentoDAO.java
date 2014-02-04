package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(TipoModeloDocumentoDAO.NAME)
@AutoCreate
public class TipoModeloDocumentoDAO extends DAO<TipoModeloDocumento> {

	public static final String NAME = "tipoModeloDocumentoDAO";
	private static final long serialVersionUID = 1L;
}
