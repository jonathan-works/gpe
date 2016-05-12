package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.fluxo.entity.VariavelClassificacaoDocumento;
import br.com.infox.epp.fluxo.query.VariavelClassificacaoDocumentoQuery;

@Name(VariavelClassificacaoDocumentoDAO.NAME)
@AutoCreate
@Stateless
public class VariavelClassificacaoDocumentoDAO extends DAO<VariavelClassificacaoDocumento> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "variavelClassificacaoDocumentoDAO";

    public List<VariavelClassificacaoDocumento> listVariavelClassificacao(String nomeVariavel, Integer idFluxo) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, nomeVariavel);
        return getNamedResultList(VariavelClassificacaoDocumentoQuery.VARIAVEL_CLASSIFICACAO_LIST, params);
    }
    
    public List<ClassificacaoDocumento> listClassificacoesPublicadasDaVariavel(String nomeVariavel, Integer idFluxo) {
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
        }
    }
    
    
    public VariavelClassificacaoDocumento findVariavelClassificacao(Integer idFluxo, String variavel, ClassificacaoDocumento classificacao) {
        Map<String, Object> params = new HashMap<>();
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_ID_FLUXO, idFluxo);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_VARIAVEL, variavel);
        params.put(VariavelClassificacaoDocumentoQuery.PARAM_CLASSIFICACAO_DOCUMENTO, classificacao);
        return getNamedSingleResult(VariavelClassificacaoDocumentoQuery.FIND_VARIAVEL_CLASSIFICACAO, params);
    }
    
    public List<ClassificacaoDocumento> listClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento, int start, int max) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<ClassificacaoDocumento> query = cb.createQuery(ClassificacaoDocumento.class);
    	Root<ClassificacaoDocumento> from = query.from(ClassificacaoDocumento.class);
    	
    	query.select(from);
    	query.orderBy(cb.asc(from.get("descricao")));
    	
    	query.where(createRestrictions(cb, query, idFluxo, variavel, tipoDocumento, nomeClassificacaoDocumento));
    	
    	return getEntityManager().createQuery(query).setMaxResults(max).setFirstResult(start).getResultList();
    }
    
    public Long totalClassificacoesDisponiveisParaVariavel(Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Long> query = cb.createQuery(Long.class);
    	Root<ClassificacaoDocumento> from = query.from(ClassificacaoDocumento.class);
    	query.select(cb.count(from));
    	
    	query.where(createRestrictions(cb, query, idFluxo, variavel, tipoDocumento, nomeClassificacaoDocumento));
    	
    	return getEntityManager().createQuery(query).getSingleResult();
    }
    
    protected Predicate createRestrictions(CriteriaBuilder cb, CriteriaQuery<?> query, Integer idFluxo, String variavel, TipoDocumentoEnum tipoDocumento, String nomeClassificacaoDocumento) {
    	Root<?> from = query.getRoots().iterator().next();
		Predicate predicate = cb.and(cb.equal(from.get("ativo"), true),
    			cb.equal(from.get("sistema"), false),
    			from.get("inTipoDocumento").in(TipoDocumentoEnum.T, tipoDocumento));
    	
    	Subquery<VariavelClassificacaoDocumento> subquery = query.subquery(VariavelClassificacaoDocumento.class);
    	Root<VariavelClassificacaoDocumento> subFrom = subquery.from(VariavelClassificacaoDocumento.class);
    	subquery.select(subFrom);
    	subquery.where(cb.equal(subFrom.get("classificacaoDocumento"), from),
    			cb.equal(subFrom.get("removerNaPublicacao"), false),
    			cb.equal(subFrom.get("variavel"), variavel),
    			cb.equal(subFrom.get("fluxo"), idFluxo));
    	
    	predicate = cb.and(predicate, cb.not(cb.exists(subquery)));
    	if (nomeClassificacaoDocumento != null) {
    		Path<String> descricao = from.get("descricao");
    		predicate = cb.and(predicate, cb.like(cb.lower(descricao), "%" + nomeClassificacaoDocumento.toLowerCase() + "%"));
    	}
		return predicate;
	}
}
