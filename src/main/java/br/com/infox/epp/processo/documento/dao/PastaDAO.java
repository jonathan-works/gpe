package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.PastaQuery.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Pasta;
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
		return ((Number) getNamedSingleResult(TOTAL_DOCUMENTOS_PASTA, parameters)).intValue();
	}
	
	public int getTotalDocumentosPasta(Pasta pasta, String customFilter, Map<String, Object> params) {
		Map<String, Object> parameters = new HashMap<>();
		if (params != null) {
			parameters.putAll(params);
		}
		parameters.put(PARAM_PASTA, pasta);
		parameters.put(PARAM_LOCALIZACAO, Authenticator.getLocalizacaoAtual());
		return ((Number) getSingleResult(TOTAL_DOCUMENTOS_PASTA_QUERY + customFilter, parameters)).intValue();
	}
}
