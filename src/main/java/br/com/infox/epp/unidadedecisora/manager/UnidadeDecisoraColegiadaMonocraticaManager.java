package br.com.infox.epp.unidadedecisora.manager;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraColegiadaMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(UnidadeDecisoraColegiadaMonocraticaManager.NAME)
@Stateless
public class UnidadeDecisoraColegiadaMonocraticaManager extends Manager<UnidadeDecisoraColegiadaMonocraticaDAO, UnidadeDecisoraColegiadaMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaMonocraticaManager";
	
}
