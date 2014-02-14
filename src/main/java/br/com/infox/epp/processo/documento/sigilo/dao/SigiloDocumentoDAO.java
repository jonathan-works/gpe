package br.com.infox.epp.processo.documento.sigilo.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoQuery;

@Name(SigiloDocumentoDAO.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class SigiloDocumentoDAO extends DAO<SigiloDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sigiloDocumentoDAO";
	
	public SigiloDocumento getSigiloDocumentoAtivo(ProcessoDocumento documento) {
		Map<String, Object> params = new HashMap<>();
		params.put(SigiloDocumentoQuery.QUERY_PARAM_DOCUMENTO, documento);
		return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO, params);
	}
	
	public SigiloDocumento getSigiloDocumentoAtivo(Integer idDocumento) {
		Map<String, Object> params = new HashMap<>();
		params.put(SigiloDocumentoQuery.QUERY_PARAM_ID_DOCUMENTO, idDocumento);
		return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO, params);
	}
	
	public boolean isSigiloso(Integer idDocumento) {
		Map<String, Object> params = new HashMap<>();
		params.put(SigiloDocumentoQuery.QUERY_PARAM_ID_DOCUMENTO, idDocumento);
		return getNamedSingleResult(SigiloDocumentoQuery.NAMED_QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO, params) != null;
	}

	public void inativarSigilos(ProcessoDocumento documento) {
		Map<String, Object> params = new HashMap<>();
		params.put(SigiloDocumentoQuery.QUERY_PARAM_DOCUMENTO, documento);
		executeNamedQueryUpdate(SigiloDocumentoQuery.NAMED_QUERY_INATIVAR_SIGILOS, params);
	}
}
