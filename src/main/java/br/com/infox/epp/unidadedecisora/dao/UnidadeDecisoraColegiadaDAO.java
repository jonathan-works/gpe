package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.ID_USUARIO_LOGIN;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_EXISTE_UDC_BY_LOCALIZACAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@AutoCreate
@Name(UnidadeDecisoraColegiadaDAO.NAME)
public class UnidadeDecisoraColegiadaDAO extends DAO<UnidadeDecisoraColegiada> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaDAO";
	
	public List<Map<String, String>> searchUnidadeDecisoraColegiadaWithIdUsuario(Integer idUsuario){
		Map<String, Object> map = new HashMap<>(1);
		map.put(ID_USUARIO_LOGIN, idUsuario);
		return getNamedResultList(SEARCH_UDC_BY_USUARIO, map);
	}
	
	public boolean existeUnidadeDecisoraComLocalizacao(Integer idLocalizacao){
		Map<String, Object> map = new HashMap<>(1);
		map.put(ID_LOCALIZACAO, idLocalizacao);
		return (long) getNamedSingleResult(SEARCH_EXISTE_UDC_BY_LOCALIZACAO, map) > 0;
	}

}
