package br.com.infox.epp.documento.dao;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.query.TipoModeloDocumentoQuery;

@Stateless
@AutoCreate
@Name(TipoModeloDocumentoDAO.NAME)
public class TipoModeloDocumentoDAO extends DAO<TipoModeloDocumento> {

    public static final String NAME = "tipoModeloDocumentoDAO";
    private static final long serialVersionUID = 1L;
    
    public List<TipoModeloDocumento> getTiposModeloDocumentoAtivos() {
    	return getNamedResultList(TipoModeloDocumentoQuery.LIST_TIPOS_MODELO_DOCUMENTO_ATIVOS);
    }
}
