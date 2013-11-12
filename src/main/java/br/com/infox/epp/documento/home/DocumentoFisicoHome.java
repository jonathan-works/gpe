package br.com.infox.epp.documento.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

/**
 * 
 * @author Daniel
 *
 */
@Name(DocumentoFisicoHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Deprecated
public class DocumentoFisicoHome extends AbstractHome<DocumentoFisico> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoFisicoHome";
	
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