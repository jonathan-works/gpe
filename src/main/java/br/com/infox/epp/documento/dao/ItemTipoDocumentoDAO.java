package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;

@Name(ItemTipoDocumentoDAO.NAME)
@AutoCreate
public class ItemTipoDocumentoDAO extends DAO<ItemTipoDocumento> {

	public static final String NAME = "itemTipoDocumento";
	private static final long serialVersionUID = 1L;
}
