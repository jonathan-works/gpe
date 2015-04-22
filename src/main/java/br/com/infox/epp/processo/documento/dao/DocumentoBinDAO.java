package br.com.infox.epp.processo.documento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.query.DocumentoBinQuery;

@AutoCreate
@Name(DocumentoBinDAO.NAME)
public class DocumentoBinDAO extends DAO<DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinDAO";
    
    public DocumentoBin getByUUID(UUID uuid) {
        Map<String, Object> params = new HashMap<>();
        params.put(DocumentoBinQuery.QUERY_PARAM_UUID, uuid);
        return getNamedSingleResult(DocumentoBinQuery.GET_BY_UUID, params);
    }

	public List<Documento> getDocumentosNaoSuficientementeAssinados(DocumentoBin documentoBin) {
		Map<String, Object> params = new HashMap<>();
        params.put(DocumentoBinQuery.QUERY_PARAM_DOCUMENTO_BIN, documentoBin);
        return getNamedResultList(DocumentoBinQuery.GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS, params);
	}
	
}
