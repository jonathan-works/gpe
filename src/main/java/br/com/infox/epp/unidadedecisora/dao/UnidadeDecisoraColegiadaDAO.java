package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.ID_USUARIO_LOGIN;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_EXISTE_UDC_BY_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery;

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
	
	@Override
	public List<UnidadeDecisoraColegiada> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UnidadeDecisoraColegiada> cq = cb.createQuery(UnidadeDecisoraColegiada.class);
		Root<UnidadeDecisoraColegiada> root = cq.from(UnidadeDecisoraColegiada.class);
		cq.orderBy(cb.asc(root.get("nome")));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<UnidadeDecisoraColegiada> findAllAtivo() {
	    return getNamedResultList(UnidadeDecisoraColegiadaQuery.FIND_ALL_ATIVO);
	}

}
