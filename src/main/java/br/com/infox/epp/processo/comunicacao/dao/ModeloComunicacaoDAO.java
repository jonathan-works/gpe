package br.com.infox.epp.processo.comunicacao.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.query.ModeloComunicacaoQuery;
import br.com.infox.epp.processo.documento.entity.Documento;
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
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcessoRoot(String processoRoot) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_NUMERO_PROCESSO_ROOT, processoRoot);
		return getNamedResultList(ModeloComunicacaoQuery.LIST_BY_PROCESSO_ROOT, params);
	}
	
	public Processo getComunicacao(DestinatarioModeloComunicacao destinatario) {
		Map<String, Object> params = new HashMap<>(2);
		params.put(ModeloComunicacaoQuery.PARAM_ID_DESTINATARIO, destinatario.getId().toString());
		params.put(ModeloComunicacaoQuery.PARAM_METADADO_DESTINATARIO, ComunicacaoMetadadoProvider.DESTINATARIO.getMetadadoType());
		return getNamedSingleResult(ModeloComunicacaoQuery.GET_COMUNICACAO_DESTINATARIO, params);
	}

	public DocumentoModeloComunicacao getDocumentoInclusoPorPapel(Collection<String> identificadoresPapel, ModeloComunicacao modeloComunicacao) {
		TypedQuery<DocumentoModeloComunicacao> q = getEntityManager().createNamedQuery(ModeloComunicacaoQuery.GET_DOCUMENTO_INCLUSO_POR_PAPEL, DocumentoModeloComunicacao.class);
		q.setParameter(ModeloComunicacaoQuery.PARAM_IDENTIFICADORES_PAPEL, identificadoresPapel);
		q.setParameter(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		q.setMaxResults(1);
		List<DocumentoModeloComunicacao> result = q.getResultList();
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public List<Documento> getDocumentosByModeloComunicacao(ModeloComunicacao modeloComunicacao){
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		return getNamedResultList(ModeloComunicacaoQuery.GET_DOCUMENTOS_MODELO_COMUNICACAO, params);
	}
	
	
}
