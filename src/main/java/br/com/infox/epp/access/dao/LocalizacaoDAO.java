package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.LocalizacaoQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_CAMINHO_COMPLETO_DUPLICADO_QUERY;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_IDS;
import static br.com.infox.epp.access.query.LocalizacaoQuery.PART_FILTER_BY_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_ESTRUTURA_PAI;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_ID_LOCALIZACAO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.USOS_DA_HIERARQUIA_LOCALIZACAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.type.TipoUsoLocalizacaoEnum;

@Name(LocalizacaoDAO.NAME)
@AutoCreate
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
        params.put(QUERY_PARAM_CAMINHO_COMPLETO, localizacao.getCaminhoCompleto());
        String query = IS_CAMINHO_COMPLETO_DUPLICADO_QUERY;
        if (localizacao.getIdLocalizacao() != null) {
            params.put(QUERY_PARAM_ID_LOCALIZACAO, localizacao.getIdLocalizacao());
            query = IS_CAMINHO_COMPLETO_DUPLICADO_QUERY + PART_FILTER_BY_LOCALIZACAO;
        }
        boolean result = ((Number) getSingleResult(query, params)).longValue() > 0;
        if (result) {
            return result;
        }
        
        query = IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY;
        if (localizacao.getIdLocalizacao() != null) {
            query = IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY + PART_FILTER_BY_LOCALIZACAO;
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
    
    public List<TipoUsoLocalizacaoEnum> getUsosLocalizacao(Localizacao localizacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_CAMINHO_COMPLETO, localizacao.getCaminhoCompleto());
        List<String> result = getNamedResultList(USOS_DA_HIERARQUIA_LOCALIZACAO, params);
        List<TipoUsoLocalizacaoEnum> usos = new ArrayList<>();
        for (String s : result) {
            usos.add(TipoUsoLocalizacaoEnum.valueOf(s));
        }
        return usos;
    }
}
