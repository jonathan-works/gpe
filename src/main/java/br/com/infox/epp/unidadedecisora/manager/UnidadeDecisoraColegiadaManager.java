package br.com.infox.epp.unidadedecisora.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.unidadedecisora.dao.UnidadeDecisoraColegiadaDAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(UnidadeDecisoraColegiadaManager.NAME)
public class UnidadeDecisoraColegiadaManager extends Manager<UnidadeDecisoraColegiadaDAO, UnidadeDecisoraColegiada>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaManager";
	
	public List<Map<String, String>> getUnidadeDecisoraListByIdUsuario(Integer idUsuario){
		return getDao().searchUnidadeDecisoraColegiadaWithIdUsuario(idUsuario);
	}
	
	public boolean existeUnidadeColegiadaComLocalizacao(Integer idLocalizacao){
		return getDao().existeUnidadeDecisoraComLocalizacao(idLocalizacao);
	}

}
