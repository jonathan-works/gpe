package br.com.infox.epp.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.ItemTipoDocumentoDAO;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;

@Name(ItemTipoDocumentoManager.NAME)
@AutoCreate
public class ItemTipoDocumentoManager extends Manager<ItemTipoDocumentoDAO, ItemTipoDocumento> {
	private static final long serialVersionUID = 4455754174682600299L;
	public static final String NAME = "itemTipoDocumentoManager";
}