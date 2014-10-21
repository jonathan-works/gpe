package br.com.infox.epp.processo.documento.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
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
}
