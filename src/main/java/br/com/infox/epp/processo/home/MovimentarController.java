package br.com.infox.epp.processo.home;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.action.AnaliseDocumentoAction;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.service.ProcessoService;

@Named
@ViewScoped
public class MovimentarController implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "movimentarController";
	
	@Inject
	private ProcessoService processoService;
	@Inject
	private AnaliseDocumentoAction analiseDocumentoAction;
	
	private Processo processo;
	
	
	public boolean isTipoProcessoAnaliseDocumento() {
		return processoService.isTipoProcessoDocumento(processo);
	}
	
	public boolean isTipoProcessoComunicacao() {
		return processoService.isTipoProcessoComunicacao(processo);
	}
	
	public ProcessoService getProcessoService() {
		return processoService;
	}

	public void setProcessoService(ProcessoService processoService) {
		this.processoService = processoService;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
		if(isTipoProcessoAnaliseDocumento()){
			analiseDocumentoAction.setProcesso(getProcesso());
		}
	}
	
}
