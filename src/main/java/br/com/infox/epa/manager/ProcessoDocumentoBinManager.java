package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;

@Name(ProcessoDocumentoBinManager.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoBinManager extends GenericManager{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoDocumentoBinManager";
	
	private ProcessoDocumentoBin processoDocumentoBin = new ProcessoDocumentoBin();

	public void setProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin) {
		this.processoDocumentoBin = processoDocumentoBin;
	}

	public ProcessoDocumentoBin getProcessoDocumentoBin() {
		return processoDocumentoBin;
	}
}
