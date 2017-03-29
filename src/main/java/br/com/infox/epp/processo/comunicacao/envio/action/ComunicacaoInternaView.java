package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoInternaSearch;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoInternaService;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class ComunicacaoInternaView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ComunicacaoInternaSearch comunicacaoInternaSearch;
    @Inject
    private ComunicacaoInternaService comunicacaoInternaService;
    
    private Processo processo;
    private List<Processo> comunicacoesInternas;
    private List<ModeloComunicacao> comunicacoesInternasNaoFinalizadas;
    
    public void onSelectTab(Processo processo) {
        this.processo = processo;
        comunicacoesInternas = comunicacaoInternaSearch.getComunicacoesInternas(getProcesso().getIdProcesso());
        comunicacoesInternasNaoFinalizadas = comunicacaoInternaSearch.getComunicacoesInternasNaoFinalizadas(getProcesso().getIdProcesso());
    }
    
    public Integer getIdDocumentoComunicacao(Processo processo) {
        ProcessInstance processInstance = JbpmContext.getCurrentJbpmContext().getProcessInstance(processo.getIdJbpm());
        return (Integer) processInstance.getContextInstance().getVariable(ComunicacaoInternaService.DOCUMENTO_COMUNICACAO);
    }
    
    @ExceptionHandled(value = MethodType.REMOVE)
    public void excluirModelo(ModeloComunicacao modeloComunicacao) {
        comunicacaoInternaService.removerModeloComunicacao(modeloComunicacao);
        comunicacoesInternasNaoFinalizadas.remove(modeloComunicacao);
    }
    
    public List<String> getTaskNames(Processo processo) {
        if (processo == null) return Collections.emptyList();
        ProcessInstance processInstance = JbpmContext.getCurrentJbpmContext().getProcessInstance(processo.getIdJbpm());
        return getTaskInstancesOpenedNames(processInstance);
    }
    
    private List<String> getTaskInstancesOpenedNames(ProcessInstance processInstance) {
        Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        List<String> taskNames = new ArrayList<>();
        for (TaskInstance taskInstance : taskInstances) {
            if (!taskInstance.hasEnded()) {
                taskNames.add(taskInstance.getName());
            }
        }
        return taskNames;
    }
    
    public List<ModeloComunicacao> getComunicacoesInternasNaoFinalizadas() {
        return comunicacoesInternasNaoFinalizadas;
    }

    public List<Processo> getComunicacoesInternas() {
        return comunicacoesInternas;
    }

    private Processo getProcesso() {
        return processo;
    }
    
}
