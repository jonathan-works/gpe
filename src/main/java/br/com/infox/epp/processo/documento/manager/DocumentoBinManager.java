package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;

@Name(DocumentoBinManager.NAME)
@AutoCreate
public class DocumentoBinManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinManager";
    
    @In
    private DocumentoBinDAO documentoBinDAO;
    
    public byte[] getData(int idDocumentoBin) {
        return documentoBinDAO.getData(idDocumentoBin);
    }

}
