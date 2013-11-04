package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.Natureza;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Fluxo;

@Name(NaturezaCategoriaFluxoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaCategoriaFluxoManager extends GenericManager {

	private static final long serialVersionUID = -1441750117108371132L;

	public static final String NAME = "naturezaCategoriaFluxoManager";

	@In
	private NaturezaCategoriaFluxoDAO naturezaCategoriaFluxoDAO;
	
	public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
		return naturezaCategoriaFluxoDAO.listByNatureza(natureza);
	}

    public NaturezaCategoriaFluxo getByRelationship(Natureza natureza, Categoria categoria, Fluxo fluxo) {
        return naturezaCategoriaFluxoDAO.getByRelationship(natureza, categoria, fluxo);
    }	
}