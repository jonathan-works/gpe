package br.com.infox.epp.tce.prestacaocontas.modelo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.tce.prestacaocontas.modelo.dao.ModeloPrestacaoContasDAO;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;

@Name(ModeloPrestacaoContasManager.NAME)
@AutoCreate
public class ModeloPrestacaoContasManager extends Manager<ModeloPrestacaoContasDAO, ModeloPrestacaoContas> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasManager";
    
    public int totalResponsaveisAssociados(ModeloPrestacaoContas modelo) {
        return getDao().totalResponsaveisAssociados(modelo);
    }
    
    public int totalDocumentosAssociados(ModeloPrestacaoContas modelo) {
        return getDao().totalDocumentosAssociados(modelo);
    }
}
