package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.jbpm.fitter.TypeFitter;
import br.com.itx.util.ComponentUtil;

@Name(ProcessoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoManager extends GenericManager {
	
	private static final long serialVersionUID = 8095772422429350875L;
	public static final String NAME = "processoManager";
	
	private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
	
	public TipoProcessoDocumentoManager getTipoProcessoDocumentoManager(){
		if (tipoProcessoDocumentoManager == null)
			tipoProcessoDocumentoManager = ComponentUtil.getComponent(TipoProcessoDocumentoManager.NAME);
		return tipoProcessoDocumentoManager;
	}
	
}
