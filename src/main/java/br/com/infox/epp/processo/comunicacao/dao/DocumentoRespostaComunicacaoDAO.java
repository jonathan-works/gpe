package br.com.infox.epp.processo.comunicacao.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.query.DocumentoRespostaComunicacaoQuery;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoRespostaComunicacaoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DocumentoRespostaComunicacaoDAO extends DAO<DocumentoRespostaComunicacao> {
	public static final String NAME = "documentoRespostaComunicacaoDAO";
	private static final long serialVersionUID = 1L;
	
	public void removerDocumentoResposta(Documento documento) throws DAOException {
		Map<String, Object> params = new HashMap<>();
		params.put(DocumentoRespostaComunicacaoQuery.PARAM_DOCUMENTO, documento);
		executeNamedQueryUpdate(DocumentoRespostaComunicacaoQuery.REMOVER_DOCUMENTO_RESPOSTA, params);
	}
	
	public Processo getComunicacaoVinculada(Documento documento) {
		Map<String, Object> params = new HashMap<>();
		params.put(DocumentoRespostaComunicacaoQuery.PARAM_DOCUMENTO, documento);
		return getNamedSingleResult(DocumentoRespostaComunicacaoQuery.GET_COMUNICACAO_VINCULADA, params);
	}
}
