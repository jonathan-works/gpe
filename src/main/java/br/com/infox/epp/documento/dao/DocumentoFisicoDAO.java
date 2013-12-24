package br.com.infox.epp.documento.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.query.DocumentoFisicoQuery;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoFisicoDAO.NAME)
@AutoCreate
public class DocumentoFisicoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoFisicoDAO";

    /**
     * Lista todos os papeis relacionados a um determinado fluxo.
     * @param fluxo que se deseja obter os papeis.
     * @return
     */
    public List<DocumentoFisico> listByProcesso(Processo processo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(DocumentoFisicoQuery.QUERY_PARAM_PROCESSO, processo);
        return getNamedResultList(DocumentoFisicoQuery.LIST_BY_PROCESSO, parameters);
    }

}
