package br.com.infox.epp.processo.documento.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.processo.documento.dao.DocumentoBinarioDAO;
import br.com.infox.epp.processo.documento.service.DocumentoBinWrapper;
import br.com.infox.epp.processo.documento.service.DocumentoBinWrapperFactory;

@AutoCreate
@Stateless
@Name(DocumentoBinarioManager.NAME)
public class DocumentoBinarioManager extends Manager<DocumentoBinarioDAO, DocumentoBinario> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinarioManager";
    
    public byte[] getData(int idDocumentoBinario) {
        return getDocumentoWrapper(idDocumentoBinario).carregarDocumentoBinario().getDocumentoBinario();
    }

    public DocumentoBinario salvarBinario(int idDocumentoBinario, byte[] file) throws DAOException {
        return getDao().gravarBinario(idDocumentoBinario, file);
    }

    public void remove(Integer idDocumentoExistente) throws DAOException {
        remove(getDao().getReference(idDocumentoExistente));
    }
    
    public boolean existeBinario(int idDocumentoBinario) {
        return getDocumentoWrapper(idDocumentoBinario).existeBinario();
    }
    
    public DocumentoBinWrapper getDocumentoWrapper(Integer idDocumentoBin) {
        return DocumentoBinWrapperFactory.getInstance().createWrapperInstance(idDocumentoBin);
    }
}
