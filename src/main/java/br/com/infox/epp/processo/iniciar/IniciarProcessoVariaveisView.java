package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.StartFormData;
import br.com.infox.epp.processo.form.StartFormDataImpl;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.util.JsfUtil;

@Named
@ViewScoped
public class IniciarProcessoVariaveisView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private JsfUtil jsfUtil;
    @Inject
    private IniciarProcessoService iniciarProcessoService;
    @Inject
    private MetadadoProcessoManager metadadoProcessoManager;
    
    private Processo processo;
    private ProcessDefinition processDefinition;
    private StartFormData formData;
    
    @PostConstruct
    private void init() {
        processo = jsfUtil.getFlashParam("processo", Processo.class);
        if (processo == null) {
            jsfUtil.redirect("/Processo/listView.seam");
        } else {
            processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo());
            formData = new StartFormDataImpl(processo, processDefinition);
        }
    }
    
    @ExceptionHandled(value = MethodType.UPDATE)
    public void gravar() {
        formData.update();
    }
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    @Transactional
    public String iniciar() {
        formData.update();
        Map<String, Object> variables = formData.getVariables();
        metadadoProcessoManager.removerMetadado(EppMetadadoProvider.STATUS_PROCESSO, processo);
        processo.removerMetadado(EppMetadadoProvider.STATUS_PROCESSO);
        iniciarProcessoService.iniciarProcesso(processo, variables);
        return "/Painel/list.seam?faces-redirect=true";
    }
    
    public StartFormData getFormData() {
        return formData;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }
    
}
