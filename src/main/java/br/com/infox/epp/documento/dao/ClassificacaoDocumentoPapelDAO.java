package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;

@AutoCreate
@Name(ClassificacaoDocumentoPapelDAO.NAME)
public class ClassificacaoDocumentoPapelDAO extends DAO<ClassificacaoDocumentoPapel> {

    public static final String NAME = "classificacaoDocumentoPapelDAO";
    private static final long serialVersionUID = 1L;
}
