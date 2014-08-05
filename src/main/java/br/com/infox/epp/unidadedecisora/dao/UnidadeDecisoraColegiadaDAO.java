package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.ID_USUARIO_LOGIN;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO;

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
	
	public List<UnidadeDecisoraColegiada> searchUnidadeDecisoraColegiadaWithIdUsuario(Integer idUsuario){
		Map<String, Object> map = new HashMap<>(1);
		map.put(ID_USUARIO_LOGIN, idUsuario);
		return getNamedResultList(SEARCH_UDC_BY_USUARIO, map);
	}

}
