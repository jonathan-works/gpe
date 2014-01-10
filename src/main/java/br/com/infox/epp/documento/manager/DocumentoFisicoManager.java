package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.documento.dao.DocumentoFisicoDAO;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoFisicoManager.NAME)
@AutoCreate
public class DocumentoFisicoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	private static final Class<DocumentoFisico> CLASS = DocumentoFisico.class;

	public static final String NAME = "documentoFisicoManager";

	@In
	private DocumentoFisicoDAO documentoFisicoDAO;
	
	public List<DocumentoFisico> listByProcesso(Processo processo) {
		return documentoFisicoDAO.listByProcesso(processo);
	}
	
    public List<DocumentoFisico> findAll() {
        return findAll(CLASS);
    }
	
}