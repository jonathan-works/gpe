package br.com.infox.epp.processo.home;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.Holder;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.action.AnaliseDocumentoAction;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.service.ProcessoService;

@Named
@ViewScoped
public class MovimentarController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProcessoService processoService;
	@Inject
	private AnaliseDocumentoAction analiseDocumentoAction;
	
	private Holder<Processo> processoHolder;
	
	@Deprecated
    public void init(Processo processo) {
        init(new Holder<Processo>(processo));
    }
	
	public void init(Holder<Processo> processoHolder) {
	    this.processoHolder = processoHolder;
	    if(isTipoProcessoAnaliseDocumento()){
            analiseDocumentoAction.setProcesso(getProcesso());
        }
	}
	
	public boolean isTipoProcessoAnaliseDocumento() {
		return processoService.isTipoProcessoDocumento(getProcesso());
	}
	
	public boolean isTipoProcessoComunicacao() {
		return processoService.isTipoProcessoComunicacao(getProcesso());
	}
	
	public ProcessoService getProcessoService() {
		return processoService;
	}

	public void setProcessoService(ProcessoService processoService) {
		this.processoService = processoService;
	}

	public Processo getProcesso() {
		return processoHolder != null ? processoHolder.value : null;
	}

}
