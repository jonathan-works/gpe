package br.com.infox.epa.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.manager.ModeloDocumentoManager;
import br.com.infox.epa.manager.ProcessoDocumentoBinManager;
import br.com.infox.epa.manager.ProcessoDocumentoManager;
import br.com.infox.epa.manager.TipoProcessoDocumentoManager;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoEvento;
import br.com.infox.ibpm.jbpm.fitter.TypeFitter;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ProcessoService.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoService extends GenericManager {
	
	private static final long serialVersionUID = 8095772422429350875L;
	public static final String NAME = "processoManager";
	
	@In private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
	@In private ProcessoDocumentoManager processoDocumentoManager;
	@In private ProcessoDocumentoBinManager processoDocumentoBinManager;
	@In private ModeloDocumentoManager modeloDocumentoManager;
	
	public void limpar(){
		modeloDocumentoManager.limpar();
		tipoProcessoDocumentoManager.limpar();
	}
	
}
