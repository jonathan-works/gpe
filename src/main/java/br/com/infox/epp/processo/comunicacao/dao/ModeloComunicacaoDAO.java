package br.com.infox.epp.processo.comunicacao.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.query.ModeloComunicacaoQuery;
import br.com.infox.epp.processo.entity.Processo;

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
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcesso(Processo processo) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_PROCESSO, processo);
		return getNamedResultList(ModeloComunicacaoQuery.LIST_BY_PROCESSO, params);
	}
	
	public Processo getComunicacao(DestinatarioModeloComunicacao destinatario) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_ID_DESTINATARIO, destinatario.getId().toString());
		return getNamedSingleResult(ModeloComunicacaoQuery.GET_COMUNICACAO_DESTINATARIO, params);
	}
}
