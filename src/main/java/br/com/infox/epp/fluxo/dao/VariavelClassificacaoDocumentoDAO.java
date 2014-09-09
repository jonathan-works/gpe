package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.fluxo.entity.VariavelClassificacaoDocumento;
import br.com.infox.epp.fluxo.query.VariavelClassificacaoDocumentoQuery;

@Name(VariavelClassificacaoDocumentoDAO.NAME)
@AutoCreate
public class VariavelClassificacaoDocumentoDAO extends DAO<VariavelClassificacaoDocumento> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "variavelClassificacaoDocumentoDAO";

    public List<VariavelClassificacaoDocumento> listVariavelClassificacao(String nomeVariavel, Integer idFluxo) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, nomeVariavel);
        return getNamedResultList(VariavelClassificacaoDocumentoQuery.VARIAVEL_CLASSIFICACAO_LIST, params);
    }
    
    public List<TipoProcessoDocumento> listClassificacoesPublicadasDaVariavel(String nomeVariavel, Integer idFluxo) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, nomeVariavel);
        return getNamedResultList(VariavelClassificacaoDocumentoQuery.CLASSIFICACOES_PUBLICADAS_DA_VARIAVEL, params);
    }
    
    public void publicarClassificacoesDasVariaveis(Integer idFluxo) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        executeNamedQueryUpdate(VariavelClassificacaoDocumentoQuery.PUBLICAR, params);
    }

    public void removerClassificacoesDeVariaveisObsoletas(Integer idFluxo, List<String> variaveisExistentes) throws DAOException {
        String hql = VariavelClassificacaoDocumentoQuery.REMOVER_CLASSIFICACOES_VARIAVEIS_OBSOLETAS_BASE_QUERY;
        boolean removerVariaveisNaoExistentes = variaveisExistentes != null && !variaveisExistentes.isEmpty();
        if (removerVariaveisNaoExistentes) {
            hql += VariavelClassificacaoDocumentoQuery.REMOVER_CLASSIFICACOES_VARIAVEIS_OBSOLETAS_VARIAVEIS_EXISTENTES_PART;
        } else {
            hql += ")";
        }
        Query query = getEntityManager().createQuery(hql).setParameter(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        if (removerVariaveisNaoExistentes) {
            query.setParameter(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEIS, variaveisExistentes);
        }
        try {
            query.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            rollbackTransactionIfNeeded();
        }
    }
    
    public List<TipoProcessoDocumento> listClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento, int start, int max) {
        String hql = VariavelClassificacaoDocumentoQuery.CLASSIFICACOES_DISPONIVEIS_PARA_VARIAVEL_BASE_QUERY;
        if (nomeClassificacaoDocumento != null) {
            hql += VariavelClassificacaoDocumentoQuery.NOME_CLASSIFICACAO_FILTER;
        }
        hql += VariavelClassificacaoDocumentoQuery.ORDER_BY_NOME_CLASSIFICACAO;
        TypedQuery<TipoProcessoDocumento> query = getEntityManager().createQuery(hql, TipoProcessoDocumento.class);
        query.setFirstResult(start).setMaxResults(max)
            .setParameter(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo)
            .setParameter(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, variavel)
            .setParameter(VariavelClassificacaoDocumentoQuery.PARAM_TIPO_DOCUMENTO, tipoDocumento);
        if (nomeClassificacaoDocumento != null) {
            query.setParameter(VariavelClassificacaoDocumentoQuery.PARAM_NOME_CLASSIFICACAO_DOCUMENTO, nomeClassificacaoDocumento);
        }
        return query.getResultList();
    }
    
    public int totalClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, variavel);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_TIPO_DOCUMENTO, tipoDocumento);
        String hql = VariavelClassificacaoDocumentoQuery.TOTAL_CLASSIFICACOES_DISPONIVEIS_PARA_VARIAVEL_QUERY;
        if (nomeClassificacaoDocumento != null) {
            hql += VariavelClassificacaoDocumentoQuery.NOME_CLASSIFICACAO_FILTER;
            params.put(VariavelClassificacaoDocumentoQuery.PARAM_NOME_CLASSIFICACAO_DOCUMENTO, nomeClassificacaoDocumento);
        }
        return ((Number) getSingleResult(hql, params)).intValue();
    }
    
    public VariavelClassificacaoDocumento findVariavelClassificacao(Integer idFluxo, String variavel, TipoProcessoDocumento classificacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, variavel);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacao);
        return getNamedSingleResult(VariavelClassificacaoDocumentoQuery.FIND_VARIAVEL_CLASSIFICACAO, params);
    }
}
