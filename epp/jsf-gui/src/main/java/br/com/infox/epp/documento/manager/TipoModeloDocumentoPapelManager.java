package br.com.infox.epp.documento.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.TipoModeloDocumentoPapelDAO;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Name(TipoModeloDocumentoPapelManager.NAME)
@AutoCreate
@Stateless
public class TipoModeloDocumentoPapelManager extends Manager<TipoModeloDocumentoPapelDAO, TipoModeloDocumentoPapel> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoModeloDocumentoPapelManager";

    public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos() {
        return getDao().getTiposModeloDocumentoPermitidos();
    }

}
