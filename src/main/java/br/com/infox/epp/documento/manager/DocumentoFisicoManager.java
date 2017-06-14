package br.com.infox.epp.documento.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.documento.dao.DocumentoFisicoDAO;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoFisicoManager.NAME)
@AutoCreate
@Stateless
public class DocumentoFisicoManager extends Manager<DocumentoFisicoDAO, DocumentoFisico> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "documentoFisicoManager";

    @Inject
    private DocumentoFisicoDAO documentoFisicoDAO;

    public List<DocumentoFisico> listByProcesso(Processo processo) {
        return documentoFisicoDAO.listByProcesso(processo);
    }
}
