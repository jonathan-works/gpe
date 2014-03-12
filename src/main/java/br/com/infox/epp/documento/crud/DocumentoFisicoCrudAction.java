package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.manager.DocumentoFisicoManager;

@Name(DocumentoFisicoCrudAction.NAME)
public class DocumentoFisicoCrudAction extends AbstractCrudAction<DocumentoFisico, DocumentoFisicoManager> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "documentoFisicoCrudAction";

    private List<DocumentoFisico> documentoFisicoList;

    public List<DocumentoFisico> getDocumentoFisicoList() {
        if (documentoFisicoList == null) {
            setDocumentoFisicoList(getManager().findAll());
        }
        return documentoFisicoList;
    }

    public void setDocumentoFisicoList(List<DocumentoFisico> documentoFisicoList) {
        this.documentoFisicoList = documentoFisicoList;
    }

}
