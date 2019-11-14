package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.LocalizacaoQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_FILHO_PARAM;
import static br.com.infox.epp.access.query.LocalizacaoQuery.ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_CAMINHO_COMPLETO_DUPLICADO_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LIST_BY_NOME_ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_CODIGO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_BY_NOME;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_DENTRO_ESTRUTURA;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_FORA_ESTRUTURA_BY_NOME;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_ESTRUTURA_FILHO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_IDS;
import static br.com.infox.epp.access.query.LocalizacaoQuery.PART_FILTER_BY_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_CODIGO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_ID_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.USOS_DA_HIERARQUIA_LOCALIZACAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.type.TipoUsoLocalizacaoEnum;

@Stateless
@AutoCreate
@Name(LocalizacaoDAO.NAME)
public class LocalizacaoDAO extends DAO<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoDAO";

    public List<Localizacao> getLocalizacoes(final Collection<Integer> ids) {
        final Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_ID_LOCALIZACAO, ids);
        return getNamedResultList(LOCALIZACOES_BY_IDS, params);
    }

    public boolean isLocalizacaoAncestor(final Localizacao localizacaoAncestor,
            final Localizacao localizacao) {
        final Map<String, Object> params = new HashMap<>();
        params.put(CAMINHO_COMPLETO, localizacaoAncestor.getCaminhoCompleto());
        params.put(LOCALIZACAO_ATTRIBUTE, localizacao);
        return getNamedSingleResult(IS_LOCALIZACAO_ANCESTOR, params) != null;
    }

    public boolean isCaminhoCompletoDuplicado(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_CAMINHO_COMPLETO,
                localizacao.getCaminhoCompleto());
        String query = IS_CAMINHO_COMPLETO_DUPLICADO_QUERY;
        if (localizacao.getIdLocalizacao() != null) {
            params.put(QUERY_PARAM_ID_LOCALIZACAO,
                    localizacao.getIdLocalizacao());
            query = IS_CAMINHO_COMPLETO_DUPLICADO_QUERY
                    + PART_FILTER_BY_LOCALIZACAO;
        }
        boolean result = ((Number) getSingleResult(query, params)).longValue() > 0;
        if (result) {
            return result;
        }

        query = IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY;
        if (localizacao.getIdLocalizacao() != null) {
            query = IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY
                    + PART_FILTER_BY_LOCALIZACAO;
        }
        params.put(QUERY_PARAM_ESTRUTURA_PAI, localizacao.getEstruturaPai());
        return ((Number) getSingleResult(query, params)).longValue() > 0;
    }

    @Override
    public Localizacao persist(Localizacao object) throws DAOException {
        if (!isCaminhoCompletoDuplicado(object)) {
            return super.persist(object);
        }
        throw new DAOException(DAOException.MSG_UNIQUE_VIOLATION);
    }

    @Override
    public Localizacao update(Localizacao object) throws DAOException {
        if (!isCaminhoCompletoDuplicado(object)) {
            return super.update(object);
        }
        throw new DAOException(DAOException.MSG_UNIQUE_VIOLATION);
    }

    public List<TipoUsoLocalizacaoEnum> getUsosLocalizacao(
            Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_CAMINHO_COMPLETO,
                localizacao.getCaminhoCompleto());
        List<String> result = getNamedResultList(
                USOS_DA_HIERARQUIA_LOCALIZACAO, params);
        List<TipoUsoLocalizacaoEnum> usos = new ArrayList<>();
        for (String s : result) {
            usos.add(TipoUsoLocalizacaoEnum.valueOf(s));
        }
        return usos;
    }

    public Localizacao getlocalizacaoByNomeEstruturaPai(String nomeEstruturaPai) {
        Map<String, Object> params = new HashMap<>();
        params.put(ESTRUTURA_PAI, nomeEstruturaPai);
        return getNamedSingleResult(LIST_BY_NOME_ESTRUTURA_PAI, params);
    }

    public Localizacao getLocalizacaoDentroEstrutura(String nomeLocalizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_LOCALIZACAO, nomeLocalizacao);
        return getNamedSingleResult(LOCALIZACAO_DENTRO_ESTRUTURA, params);
    }
    
    public Localizacao getLocalizacaoForaEstruturaByNome(String nomeLocalizacao){
    	Map<String, Object> params = new HashMap<>(1);
        params.put(QUERY_PARAM_LOCALIZACAO, nomeLocalizacao);
        return getNamedSingleResult(LOCALIZACAO_FORA_ESTRUTURA_BY_NOME, params);
    }
    
    public Localizacao getLocalizacaoByCodigo(String codigo) {
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put(QUERY_PARAM_CODIGO, codigo);
    	return getNamedSingleResult(LOCALIZACAO_BY_CODIGO, parameters);
    }

	public Localizacao getLocalizacaoByNome(String nomeLocalizacao) throws DAOException {
		TypedQuery<Localizacao> query = getEntityManager().createNamedQuery(LOCALIZACAO_BY_NOME, Localizacao.class);
		query.setParameter(QUERY_PARAM_LOCALIZACAO, nomeLocalizacao);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (NonUniqueResultException e) {
			throw new DAOException(e);
		}
	}
	
	public List<Localizacao> getLocalizacoesByEstruturaFilho(Estrutura estruturaFilho) {
		Map<String, Object> params = new HashMap<>();
		params.put(ESTRUTURA_FILHO_PARAM, estruturaFilho);
		return getNamedResultList(LOCALIZACOES_BY_ESTRUTURA_FILHO, params);
	}
}
