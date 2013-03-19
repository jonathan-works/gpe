package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoManager.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoProcessoDocumentoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoManager";
	
	private TipoProcessoDocumento tipoProcessoDocumento;
	private TipoProcessoDocumento tipoProcessoDocumentoRO;

}
