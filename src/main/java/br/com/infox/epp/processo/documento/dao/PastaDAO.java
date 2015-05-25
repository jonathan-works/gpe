package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.PastaQuery.*;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO_AND_DESCRICAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.PARAM_DESCRICAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.TOTAL_DOCUMENTOS_PASTA_QUERY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.filter.DocumentoFilter;
import br.com.infox.epp.processo.entity.Processo;

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
		return ((Number) getSingleResult(TOTAL_DOCUMENTOS_PASTA_QUERY + FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR + FILTER_EXCLUIDO + FILTER_SIGILO, parameters)).intValue();
	}
	
	public int getTotalDocumentosPastaPorFiltros(Pasta pasta, DocumentoFilter documentoFilter) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PASTA, pasta);
		String baseQuery = TOTAL_DOCUMENTOS_PASTA_QUERY;
		if (documentoFilter.getIdClassificacaoDocumento() != null) {
			baseQuery = baseQuery + FILTER_CLASSIFICACAO_DOCUMENTO;
			parameters.put(PARAM_CLASSIFICACAO_DOCUMENTO, documentoFilter.getIdClassificacaoDocumento());
		}
		if (documentoFilter.getNumeroDocumento() != null) {
			baseQuery = baseQuery + FILTER_NUMERO_DOCUMENTO;
			parameters.put(PARAM_NUMERO_DOCUMENTO, documentoFilter.getNumeroDocumento());
		}
		parameters.put(PARAM_LOCALIZACAO, Authenticator.getLocalizacaoAtual());
		return ((Number) getSingleResult(baseQuery + FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR + FILTER_EXCLUIDO + FILTER_SIGILO, parameters)).intValue();
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
}
