package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.dao.HistoricoModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.ModeloDocumento;

@Name(HistoricoModeloDocumentoManager.NAME)
@AutoCreate
public class HistoricoModeloDocumentoManager extends GenericManager {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoModeloDocumentoManager";
    
    @In private HistoricoModeloDocumentoDAO historicoModeloDocumentoDAO;
    
    public List<ModeloDocumento> listModelosDoHistorico(){
        return historicoModeloDocumentoDAO.listModelosDoHistorico();
    }
    
    public List<UsuarioLogin> listUsuariosQueAlteraramModelo(ModeloDocumento modeloDocumento){
        return historicoModeloDocumentoDAO.listUsuariosQueAlteraramModelo(modeloDocumento);
    }

}
