package br.com.infox.epp.processo.comunicacao.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.query.ModeloComunicacaoQuery;

@Name(ModeloComunicacaoDAO.NAME)
@AutoCreate
public class ModeloComunicacaoDAO extends DAO<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoDAO";
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		return getNamedSingleResult(ModeloComunicacaoQuery.IS_EXPEDIDA, params) == null;
	}
}
