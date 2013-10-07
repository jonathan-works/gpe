package br.com.infox.epp.action.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.entity.DocumentoFisico;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

/**
 * 
 * @author Daniel
 *
 */
@Name(DocumentoFisicoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoFisicoAction extends AbstractHome<DocumentoFisico> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoFisicoAction";
	
	private List<DocumentoFisico> documentoFisicoList; 
	
	@Override
	public void create() {
		super.create();
		
		setDocumentoFisicoList(EntityUtil.getEntityList(DocumentoFisico.class));
	}

	public List<DocumentoFisico> getDocumentoFisicoList() {
		return documentoFisicoList;
	}
	public void setDocumentoFisicoList(List<DocumentoFisico> documentoFisicoList) {
		this.documentoFisicoList = documentoFisicoList;
	}
	
	
	
}