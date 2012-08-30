package br.com.infox.ibpm.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.dao.DocumentoFisicoDAO;
import br.com.infox.ibpm.entity.DocumentoFisico;
import br.com.infox.ibpm.entity.Processo;

@Name(DocumentoFisicoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DocumentoFisicoManager {

	public static final String NAME = "documentoFisicoManager";

	@In
	private DocumentoFisicoDAO documentoFisicoDAO;
	
	public List<DocumentoFisico> listByProcesso(Processo processo) {
		return documentoFisicoDAO.listByProcesso(processo);
	}
	
}