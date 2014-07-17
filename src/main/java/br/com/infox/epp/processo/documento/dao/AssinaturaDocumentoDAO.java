package br.com.infox.epp.processo.documento.dao;

import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery;

@Name(AssinaturaDocumentoDAO.NAME)
public class AssinaturaDocumentoDAO extends DAO<AssinaturaDocumento> {
    public static final String NAME = "assinaturaDocumentoDAO";
    private static final long serialVersionUID = 1L;
    
    public List<AssinaturaDocumento> listAssinaturaDocumentoByProcessoDocumento(ProcessoDocumento processoDocumento) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AssinaturaDocumentoQuery.PARAM_PROCESSO_DOCUMENTO, processoDocumento);
        return getNamedResultList(AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_PROCESSO_DOCUMENTO, hashMap);
    }

}
