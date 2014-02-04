package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;

@Name(DocumentoBinManager.NAME)
@AutoCreate
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinManager";
    
    public byte[] getData(int idDocumentoBin) {
        return getDao().getData(idDocumentoBin);
    }
    
    public DocumentoBin salvarBinario(int idDocumentoBin, byte[] file) throws DAOException{
        return getDao().gravarBinario(idDocumentoBin, file);
    }

}
