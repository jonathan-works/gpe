package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaFluxoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaCategoriaFluxoManager extends GenericManager {

	public static final String NAME = "naturezaCategoriaFluxoManager";

	@In
	private NaturezaCategoriaFluxoDAO naturezaCategoriaFluxoDAO;
	
	public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
		return naturezaCategoriaFluxoDAO.listByNatureza(natureza);
	}
	
}