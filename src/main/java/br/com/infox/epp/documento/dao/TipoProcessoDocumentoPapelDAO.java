package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;

@Name(TipoProcessoDocumentoPapelDAO.NAME)
@AutoCreate
public class TipoProcessoDocumentoPapelDAO extends DAO<ClassificacaoDocumentoPapel> {

    public static final String NAME = "tipoProcessoDocumentoPapelDAO";
    private static final long serialVersionUID = 1L;
}
