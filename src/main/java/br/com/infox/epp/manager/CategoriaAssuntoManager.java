package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.CategoriaAssuntoDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaAssunto;

@Name(CategoriaAssuntoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaAssuntoManager extends GenericManager{

	private static final long serialVersionUID = -3580636874720809514L;

	public static final String NAME = "categoriaAssuntoManager";

	@In
	private CategoriaAssuntoDAO categoriaAssuntoDAO;
	
	public List<CategoriaAssunto> listByCategoria(Categoria categoria) {
		return categoriaAssuntoDAO.listByCategoria(categoria);
	}
	
}