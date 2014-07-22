package br.com.infox.epp.unidadedecisora.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(UnidadeDecisoraMonocraticaManager.NAME)
public class UnidadeDecisoraMonocraticaManager extends Manager<UnidadeDecisoraMonocraticaDAO, UnidadeDecisoraMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaManager";
	

	public boolean existeUnidadeDecisoraMonocraticaComHierarquiaLocalizacao(Localizacao localizacao) {
	    return getDao().existeUnidadeDecisoraMonocraticaComHierarquiaLocalizacao(localizacao);
	}
}
