package br.com.infox.epp.processo.documento.manager;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.dao.DocumentoBinarioDAO;
import br.com.infox.epp.processo.documento.service.DocumentoBinService;

@AutoCreate
@Name(DocumentoBinarioManager.NAME)
@Stateless
public class DocumentoBinarioManager extends Manager<DocumentoBinarioDAO, DocumentoBinario> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinarioManager";
    
    @Inject
    private DocumentoBinService documentoBinService;
    

    public byte[] getData(int idDocumentoBinario) {
        return documentoBinService.carregarDocumentoBinario(idDocumentoBinario).getDocumentoBinario();
    }

    public DocumentoBinario salvarBinario(int idDocumentoBinario, byte[] file) throws DAOException {
        return getDao().gravarBinario(idDocumentoBinario, file);
    }

    public void remove(Integer idDocumentoExistente) throws DAOException {
        remove(getDao().getReference(idDocumentoExistente));
    }
    
    public boolean existeBinario(int idDocumentoBinario) {
        return documentoBinService.existeBinario(idDocumentoBinario);
    }
}
