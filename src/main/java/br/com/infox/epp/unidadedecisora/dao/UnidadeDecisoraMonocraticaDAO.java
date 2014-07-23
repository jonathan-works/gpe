package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_UNIDADE_DEC_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_BY_UNIDADE_DECISORA_COLEGIADA;

import java.util.List;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@AutoCreate
@Name(UnidadeDecisoraMonocraticaDAO.NAME)
public class UnidadeDecisoraMonocraticaDAO extends DAO<UnidadeDecisoraMonocratica>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaDAO";

	
	public List<UnidadeDecisoraMonocratica> searchUnidadeDecisoraMonocraticaAtivoWithIdColegiada(Integer idColegiada){
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put(ID_UNIDADE_DEC_COLEGIADA, idColegiada);
		return getNamedResultList(SEARCH_BY_UNIDADE_DECISORA_COLEGIADA, map);
	}
}
