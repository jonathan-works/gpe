package br.com.infox.epp.documento.dao;

import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.PAPEL_PARAM;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.TIPOS_MODELO_DOCUMENTO_PERMITIDOS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Stateless
@AutoCreate
@Name(TipoModeloDocumentoPapelDAO.NAME)
public class TipoModeloDocumentoPapelDAO extends DAO<TipoModeloDocumentoPapel> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoModeloDocumentoPapelDAO";

    /**
     * Retorna uma lista com os tipos de modelo de documento que o perfil
     * (localização+papel) do usuário logado possue permissão para acessar.
     * */
    public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PAPEL_PARAM, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getPapel());
        return getNamedResultList(TIPOS_MODELO_DOCUMENTO_PERMITIDOS, parameters);
    }

}
