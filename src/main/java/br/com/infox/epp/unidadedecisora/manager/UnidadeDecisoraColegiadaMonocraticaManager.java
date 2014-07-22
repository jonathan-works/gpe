package br.com.infox.epp.unidadedecisora.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraColegiadaMonocraticaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(UnidadeDecisoraColegiadaMonocraticaManager.NAME)
public class UnidadeDecisoraColegiadaMonocraticaManager extends Manager<UnidadeDecisoraColegiadaMonocraticaDAO, UnidadeDecisoraColegiadaMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaMonocraticaManager";
	
	public List<UnidadeDecisoraMonocratica> getListUnidadeDecisoraMonocraticaAtivo(Integer idColegiada){
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put("id", idColegiada);
		return getDao().getResultList("select udm " + 
									  "from UnidadeDecisoraColegiadaMonocratica udcm  " +
									  "inner join udcm.unidadeDecisoraMonocratica udm " +
									  "where udm.ativo = true " +
									  "and udcm.unidadeDecisoraColegiada.idUnidadeDecisoraColegiada <> :id", map);
	}

}
