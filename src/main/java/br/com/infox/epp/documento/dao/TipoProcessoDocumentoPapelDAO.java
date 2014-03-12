package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;

@Name(TipoProcessoDocumentoPapelDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoPapelDAO extends DAO<TipoProcessoDocumentoPapel> {

    public static final String NAME = "tipoProcessoDocumentoPapelDAO";
    private static final long serialVersionUID = 1L;
}
