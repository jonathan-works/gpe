package br.com.infox.epp.processo.iniciar;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.StartFormData;
import br.com.infox.epp.processo.form.StartFormDataImpl;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.ibpm.util.JbpmUtil;

@Named
@ViewScoped
public class IniciarProcessoVariaveisView extends AbstractIniciarProcesso {

    private static final long serialVersionUID = 1L;

    @Inject
    private IniciarProcessoService iniciarProcessoService;
    
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
    
    @ExceptionHandled(createLogErro = true)
    public String iniciar() {
        formData.update();
        Map<String, Object> variables = formData.getVariables();
        ProcessInstance processInstance = iniciarProcessoService.iniciarProcesso(processo, variables);
        openMovimentarIfAccessible(processInstance);
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
