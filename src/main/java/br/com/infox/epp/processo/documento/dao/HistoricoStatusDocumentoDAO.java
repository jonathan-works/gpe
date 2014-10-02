package br.com.infox.epp.processo.documento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.PARAM_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.PARAM_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO;
import static br.com.infox.epp.processo.documento.query.HistoricoStatusDocumentoQuery.LIST_HISTORICO_BY_DOCUMENTO;

@AutoCreate
@Name(HistoricoStatusDocumentoDAO.NAME)
public class HistoricoStatusDocumentoDAO extends DAO<HistoricoStatusDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoStatusDocumentoDAO";
	
	public boolean existeAlgumHistoricoDoDocumento(Integer idDocumento){
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put(PARAM_ID_DOCUMENTO, idDocumento);
		return (long) getNamedSingleResult(EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO, params) > 0;
	}
	
	public List<HistoricoStatusDocumento> getListHistoricoByDocumento(Documento processoDocumento){
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put(PARAM_DOCUMENTO, processoDocumento);
		return getNamedResultList(LIST_HISTORICO_BY_DOCUMENTO, params);
	}

}
