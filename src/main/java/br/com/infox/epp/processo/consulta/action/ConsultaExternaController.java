package br.com.infox.epp.processo.consulta.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;

@Scope(ScopeType.CONVERSATION)
@Name(ConsultaExternaController.NAME)
public class ConsultaExternaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaExternaController";
    public static final String TAB_VIEW = "processoExternoView";

    private boolean mostrarCaptcha = true;
    
    @In
    private DocumentoManager documentoManager;

    private Processo processo;
    
    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public void selectProcesso(Processo processo) {
        mostrarCaptcha = true;
        setTab(TAB_VIEW);
        setProcesso(processo);
    }

    public List<Documento> getAnexosPublicos(ProcessoTarefa processoTarefa) {
        return documentoManager.getAnexosPublicos(processoTarefa.getTaskInstance());
    }

    public void onClickSearchNumeroTab() {
        setProcesso(null);
    }

    public void onClickSearchParteTab() {
        setProcesso(null);
    }

	public boolean isMostrarCaptcha() {
		return mostrarCaptcha;
	}
	
	public void validateCaptcha() {
		mostrarCaptcha = false;
	}
}
