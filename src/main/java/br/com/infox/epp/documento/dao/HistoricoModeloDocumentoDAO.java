package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ModeloDocumento;

@Name(HistoricoModeloDocumentoDAO.NAME)
@AutoCreate
public class HistoricoModeloDocumentoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoModeloDocumentoDAO";
    
    public List<ModeloDocumento> listModelosDoHistorico(){
        return getNamedResultList(LIST_MODELO);
    }
    
    public List<UsuarioLogin> listUsuariosQueAlteraramModelo(ModeloDocumento modeloDocumento){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LIST_USUARIO_PARAM_MODELO, modeloDocumento);
        return getNamedResultList(LIST_USUARIO, parameters);
    }

}
