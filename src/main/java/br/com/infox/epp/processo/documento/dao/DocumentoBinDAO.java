package br.com.infox.epp.processo.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@AutoCreate
@Name(DocumentoBinDAO.NAME)
public class DocumentoBinDAO extends DAO<DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinDAO";
}
