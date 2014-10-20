package br.com.infox.epp.processo.documento.service;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@Name(DocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoService";

    @In
    private DocumentoManager documentoManager;
    
    public void setDefaultFolder(Pasta pasta) throws DAOException {
        List<Documento> documentoList = documentoManager.getListDocumentoByProcesso(pasta.getProcesso());
        for (Documento documento : documentoList) {
            documento.setPasta(pasta);
            documentoManager.update(documento);
        }
    }
}
