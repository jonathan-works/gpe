package br.com.infox.epp.tce.prestacaocontas.modelo.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;
import br.com.infox.epp.tce.prestacaocontas.modelo.query.ModeloPrestacaoContasQuery;

@Name(ModeloPrestacaoContasDAO.NAME)
public class ModeloPrestacaoContasDAO extends DAO<ModeloPrestacaoContas> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasDAO";
    
    public int totalResponsaveisAssociados(ModeloPrestacaoContas modelo) {
        Map<String, Object> params = new HashMap<>();
        params.put(ModeloPrestacaoContasQuery.PARAM_MODELO, modelo);
        return ((Number) getNamedSingleResult(ModeloPrestacaoContasQuery.TOTAL_RESPONSAVEIS_ASSOCIADOS, params)).intValue();
    }
    
    public int totalDocumentosAssociados(ModeloPrestacaoContas modelo) {
        Map<String, Object> params = new HashMap<>();
        params.put(ModeloPrestacaoContasQuery.PARAM_MODELO, modelo);
        return ((Number) getNamedSingleResult(ModeloPrestacaoContasQuery.TOTAL_DOCUMENTOS_ASSOCIADOS, params)).intValue();
    }
}
