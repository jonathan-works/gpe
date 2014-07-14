package br.com.infox.epp.unidadedecisora.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraColegiadaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@Name(UnidadeDecisoraColegiadaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UnidadeDecisoraColegiadaManager extends Manager<UnidadeDecisoraColegiadaDAO, UnidadeDecisoraColegiada>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaManager";

}
