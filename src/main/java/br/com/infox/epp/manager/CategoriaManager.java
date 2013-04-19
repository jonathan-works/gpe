package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.CategoriaDAO;

@Name(CategoriaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaManager extends GenericManager {

	private static final long serialVersionUID = 2649821908249070536L;

	public static final String NAME = "categoriaManager";

	@In
	private CategoriaDAO categoriaDAO;
	
	public List<Object[]> listProcessoByCategoria() {
		return categoriaDAO.listProcessoByCategoria();
	}
}