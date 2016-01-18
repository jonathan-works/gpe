package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import ComunicacaoInternaView.ComunicacaoInternaSearch;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoInternaService;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class ComunicacaoInternaView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ComunicacaoInternaSearch comunicacaoInternaSearch;
    
    private Processo processo;
    private List<Processo> comunicacoesInternas;
    
    public void onSelectTab(Processo processo) {
        this.processo = processo;
        comunicacoesInternas = comunicacaoInternaSearch.getComunicacoesInternas(getProcesso().getIdProcesso());
    }
    
    public Integer getIdDocumentoComunicacao(Processo processo) {
        ProcessInstance processInstance = JbpmContext.getCurrentJbpmContext().getProcessInstance(processo.getIdJbpm());
        return (Integer) processInstance.getContextInstance().getVariable(ComunicacaoInternaService.DOCUMENTO_COMUNICACAO);
    }

    public List<Processo> getComunicacoesInternas() {
        return comunicacoesInternas;
    }

    private Processo getProcesso() {
        return processo;
    }
    
}
