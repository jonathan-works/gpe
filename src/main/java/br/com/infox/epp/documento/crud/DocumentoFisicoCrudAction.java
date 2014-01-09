package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.manager.DocumentoFisicoManager;

@Name(DocumentoFisicoCrudAction.NAME)
public class DocumentoFisicoCrudAction extends AbstractCrudAction<DocumentoFisico> {
	
	public static final String NAME = "documentoFisicoCrudAction";
	
	private List<DocumentoFisico> documentoFisicoList;
	
	@In private DocumentoFisicoManager documentoFisicoManager;
	
	public List<DocumentoFisico> getDocumentoFisicoList() {
		if (documentoFisicoList == null){
			setDocumentoFisicoList(documentoFisicoManager.findAll());
		}
		return documentoFisicoList;
	}
	public void setDocumentoFisicoList(List<DocumentoFisico> documentoFisicoList) {
		this.documentoFisicoList = documentoFisicoList;
	}

}
