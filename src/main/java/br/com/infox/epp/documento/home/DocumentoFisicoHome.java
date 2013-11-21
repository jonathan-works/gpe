package br.com.infox.epp.documento.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.itx.component.AbstractHome;

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
	
}