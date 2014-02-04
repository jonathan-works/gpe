package br.com.infox.epp.fluxo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.CategoriaItemDAO;
import br.com.infox.epp.fluxo.entity.CategoriaItem;

@Name(CategoriaItemManager.NAME)
@AutoCreate
public class CategoriaItemManager extends Manager<CategoriaItemDAO, CategoriaItem>{

	private static final long serialVersionUID = -3580636874720809514L;

	public static final String NAME = "categoriaItemManager";
}