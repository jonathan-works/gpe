package br.com.infox.epp.unidadedecisora.dao;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.CODIGO_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.FIND_UDM_BY_CODIGO_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_UNIDADE_DEC_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.ID_USUARIO_LOGIN;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_BY_UNIDADE_DECISORA_COLEGIADA;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_EXISTE_UDM_BY_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.SEARCH_UDM_BY_USUARIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public List<Map<String, String>> searchUnidadeDecisoraMonocraticaWithIdUsuario(Integer idUsuario){
		Map<String, Object> map = new HashMap<>(1);
		map.put(ID_USUARIO_LOGIN, idUsuario);
		return getNamedResultList(SEARCH_UDM_BY_USUARIO, map);
	}
	
	public UnidadeDecisoraMonocratica existeUnidadeDecisoraComLocalizacao(Integer idLocalizacao){
        Map<String, Object> map = new HashMap<>(1);
        map.put(ID_LOCALIZACAO, idLocalizacao);
        return getNamedSingleResult(SEARCH_EXISTE_UDM_BY_LOCALIZACAO, map);
    }
	
	public UnidadeDecisoraMonocratica findByCodigoLocalizacao(String codigoLocalizacao) {
		Map<String, Object> params = new HashMap<>();
		params.put(CODIGO_LOCALIZACAO, codigoLocalizacao);
		return getNamedSingleResult(FIND_UDM_BY_CODIGO_LOCALIZACAO, params);
	}
}
