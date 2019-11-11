package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_MODELO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_USUARIO;
import static br.com.infox.epp.documento.query.HistoricoModeloDocumentoQuery.LIST_USUARIO_PARAM_MODELO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;

@Stateless
@AutoCreate
@Name(HistoricoModeloDocumentoDAO.NAME)
public class HistoricoModeloDocumentoDAO extends DAO<HistoricoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoModeloDocumentoDAO";

    public List<ModeloDocumento> listModelosDoHistorico() {
        return getNamedResultList(LIST_MODELO);
    }

    public List<UsuarioLogin> listUsuariosQueAlteraramModelo(
            ModeloDocumento modeloDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LIST_USUARIO_PARAM_MODELO, modeloDocumento);
        return getNamedResultList(LIST_USUARIO, parameters);
    }

}
