package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.LocalizacaoQuery.CAMINHO_COMPLETO;
import static br.com.infox.epp.access.query.LocalizacaoQuery.IS_LOCALIZACAO_ANCESTOR;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACAO_ATTRIBUTE;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_BY_IDS;
import static br.com.infox.epp.access.query.LocalizacaoQuery.LOCALIZACOES_ESTRUTURA;
import static br.com.infox.epp.access.query.LocalizacaoQuery.QUERY_PARAM_ID_LOCALIZACAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoDAO.NAME)
@AutoCreate
public class LocalizacaoDAO extends DAO<Localizacao> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "localizacaoDAO";
	
	public List<Localizacao> getLocalizacoesEstrutura(){
	    return getNamedResultList(LOCALIZACOES_ESTRUTURA);
	}

	public List<Localizacao> getLocalizacoes(final Collection<Integer> ids) {
		final Map<String, Object> params = new HashMap<>();
		params.put(QUERY_PARAM_ID_LOCALIZACAO, ids);
		return getNamedResultList(LOCALIZACOES_BY_IDS, params);
	}
	
	public boolean isLocalizacaoAncestor(final Localizacao localizacaoAncestor, final Localizacao localizacao) {
	    final Map<String, Object> params = new HashMap<>();
	    params.put(CAMINHO_COMPLETO, localizacaoAncestor.getCaminhoCompleto());
	    params.put(LOCALIZACAO_ATTRIBUTE, localizacao);
	    return getNamedSingleResult(IS_LOCALIZACAO_ANCESTOR, params) != null;
	}
}
