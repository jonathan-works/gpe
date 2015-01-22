package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.TipoModeloDocumentoDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(TipoModeloDocumentoManager.NAME)
@AutoCreate
public class TipoModeloDocumentoManager extends Manager<TipoModeloDocumentoDAO, TipoModeloDocumento> {
    private static final long serialVersionUID = 4455754174682600299L;
    public static final String NAME = "tipoModeloDocumentoManager";
    
    public List<TipoModeloDocumento> getTiposModeloDocumentoAtivos() {
    	return getDao().getTiposModeloDocumentoAtivos();
    }
}
