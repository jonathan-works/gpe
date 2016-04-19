package br.com.infox.epp.processo.consulta.action;

import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.captcha.ServicoCaptchaSessao;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;

@Scope(ScopeType.CONVERSATION)
@Name(ConsultaExternaController.NAME)
@ContextDependency
public class ConsultaExternaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaExternaController";
    public static final String TAB_VIEW = "processoExternoView";

    @In
    private DocumentoManager documentoManager;
    
    @Inject
    private ServicoCaptchaSessao servicoCaptcha;

    private Processo processo;
    
    private boolean mostrarCaptcha = true;
    
    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public void selectProcesso(Processo processo) {
        mostrarCaptcha = servicoCaptcha.isMostrarCaptcha();
        if(!mostrarCaptcha) {
    		servicoCaptcha.telaMostrada();        	
        }
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
		servicoCaptcha.captchaResolvido();
		servicoCaptcha.telaMostrada();
		mostrarCaptcha = false;
	}
}
