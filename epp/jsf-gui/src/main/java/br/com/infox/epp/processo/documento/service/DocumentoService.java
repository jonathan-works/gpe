package br.com.infox.epp.processo.documento.service;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

import javax.inject.Inject;

@Name(DocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class DocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoService";

    @Inject
    private DocumentoManager documentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    
    public void setDefaultFolder(Pasta pasta) throws DAOException {
        List<Documento> documentoList = documentoManager.getListDocumentoByProcesso(pasta.getProcesso());
        for (Documento documento : documentoList) {
            if (documento.getPasta() == null) {
                documento.setPasta(pasta);
                documentoManager.update(documento);
            }
        }
    }
    
    public void removerDocumento(Documento documento) throws DAOException {
		DocumentoBin documentoBin = documento.getDocumentoBin();
		documentoManager.remove(documento);
		removerDocumentoBin(documentoBin);
	}
    
    public void removerDocumentoBin(DocumentoBin documentoBin) throws DAOException {
    	Integer idDocumentoBin = documentoBin.getId();
		documentoBinManager.remove(documentoBin);
		if (documentoBin.getExtensao() != null) {
			documentoBinarioManager.remove(idDocumentoBin);
		}
    }
}
