package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.PastaQuery.FILTER_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.FILTER_EXCLUIDO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.FILTER_NUMERO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.FILTER_SIGILO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_NOME;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO_AND_DESCRICAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_DESCRICAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_NOME;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_NUMERO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_USUARIO_PERMISSAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.TOTAL_DOCUMENTOS_PASTA_QUERY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.documento.filter.DocumentoFilter;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;

@Stateless
@AutoCreate
@Name(PastaDAO.NAME)
public class PastaDAO extends DAO<Pasta> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaDAO";
    
    public List<Pasta> getByProcesso(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESSO, processo);
        return getNamedResultList(GET_BY_PROCESSO, parameters);
    }
    
	public int getTotalDocumentosPasta(Pasta pasta) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PASTA, pasta);
		parameters.put(PARAM_LOCALIZACAO, Authenticator.getLocalizacaoAtual());
		parameters.put(PARAM_USUARIO_PERMISSAO, Authenticator.getUsuarioLogado());
		return ((Number) getSingleResult(TOTAL_DOCUMENTOS_PASTA_QUERY + FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR + FILTER_EXCLUIDO + FILTER_SIGILO, parameters)).intValue();
	}
	
	public int getTotalDocumentosPastaPorFiltros(Pasta pasta, DocumentoFilter documentoFilter, Boolean semExcluidos) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PASTA, pasta);
		parameters.put(PARAM_LOCALIZACAO, Authenticator.getLocalizacaoAtual());
		parameters.put(PARAM_USUARIO_PERMISSAO, Authenticator.getUsuarioLogado());
		String baseQuery = appendDocumentoFilters(documentoFilter, parameters, TOTAL_DOCUMENTOS_PASTA_QUERY);
		String query = baseQuery + FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR + FILTER_SIGILO;
		if (semExcluidos) {
		    query = query + FILTER_EXCLUIDO;
		}
		return ((Number) getSingleResult(query, parameters)).intValue();
	}

	protected String appendDocumentoFilters(DocumentoFilter documentoFilter, Map<String, Object> parameters, String baseQuery) {
		if (documentoFilter.getIdClassificacaoDocumento() != null) {
			baseQuery = baseQuery + FILTER_CLASSIFICACAO_DOCUMENTO;
			parameters.put(PARAM_CLASSIFICACAO_DOCUMENTO, documentoFilter.getIdClassificacaoDocumento());
		}
		if (documentoFilter.getNumeroDocumento() != null) {
			baseQuery = baseQuery + FILTER_NUMERO_DOCUMENTO;
			parameters.put(PARAM_NUMERO_DOCUMENTO, documentoFilter.getNumeroDocumento());
		}
		return baseQuery;
	}
	
	public int getTotalDocumentosPasta(Pasta pasta, String customFilter, Map<String, Object> params) {
		Map<String, Object> parameters = new HashMap<>();
		if (params != null) {
			parameters.putAll(params);
		}
		parameters.put(PARAM_PASTA, pasta);
		return ((Number) getSingleResult(TOTAL_DOCUMENTOS_PASTA_QUERY + customFilter, parameters)).intValue();
	}
	
	// Traz a primeira que encontrar caso haja mais de uma pasta com esse nome
	public Pasta getPastaByNome(String nome, Processo processo) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_NOME, nome);
		parameters.put(PARAM_PROCESSO, processo);
		return getNamedSingleResult(GET_BY_NOME, parameters);
	}

    public Pasta getByProcessoAndDescricao(Processo processo, String descricao) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PROCESSO, processo);
        params.put(PARAM_DESCRICAO, descricao);
        return getNamedSingleResult(GET_BY_PROCESSO_AND_DESCRICAO, params);
    }

    public Boolean isPadraoEmAlgumProcesso(Pasta pasta) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        cq.select(cb.literal(1));
        Root<Pasta> p = cq.from(Pasta.class);

        Subquery<Integer> existsMetadado = cq.subquery(Integer.class);
        existsMetadado.select(cb.literal(1));
        Root<MetadadoProcesso> mp = existsMetadado.from(MetadadoProcesso.class);
        existsMetadado.where(
            cb.equal(mp.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType()),
            cb.equal(p.get(Pasta_.id).as(String.class), mp.get(MetadadoProcesso_.valor))
        );

        cq.where(cb.exists(existsMetadado), cb.equal(p.get(Pasta_.id), pasta.getId()));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return result.equals(1);
        } catch (NoResultException nre) {
            return false;
        }
    }

    public List<MetadadoProcesso> listMetadadoPastaDefault(Pasta pasta) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MetadadoProcesso> cq = cb.createQuery(MetadadoProcesso.class);
        Root<MetadadoProcesso> mp = cq.from(MetadadoProcesso.class);
        cq.select(mp);
        cq.where(cb.equal(mp.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType()),
                cb.equal(mp.get(MetadadoProcesso_.valor), pasta.getId().toString()));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
