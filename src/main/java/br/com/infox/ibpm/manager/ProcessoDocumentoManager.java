package br.com.infox.ibpm.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.ProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.Processo;

@Name(ProcessoDocumentoManager.NAME)
@AutoCreate
public class ProcessoDocumentoManager extends GenericManager {

    public static final String NAME = "processoDocumentoManager";
    private static final long serialVersionUID = 1L;
    
    @In private ProcessoDocumentoDAO processoDocumentoDAO;
    
    public List<Integer> getNextSequencial(Processo processo) {
        return processoDocumentoDAO.getNextSequencial(processo);
    }
    
    public Object getModeloDocumentoByIdProcessoDocumento(Integer idProcessoDocumento){
        return processoDocumentoDAO.getModeloDocumentoByIdProcessoDocumento(idProcessoDocumento);
    }

}
