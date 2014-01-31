package br.com.infox.epp.processo.documento.dao;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.DocumentoBin;

@Name(DocumentoBinDAO.NAME)
@AutoCreate
public class DocumentoBinDAO extends DAO<DocumentoBin, Integer> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinDAO";
    
    @In 
    private transient EntityManager entityManagerBin;
    
    @Override
    protected EntityManager getEntityManager() {
        return entityManagerBin;
    }

    public byte[] getData(int idDocumentoBin) {
        return find(idDocumentoBin).getDocumentoBin();
    }
    
}
