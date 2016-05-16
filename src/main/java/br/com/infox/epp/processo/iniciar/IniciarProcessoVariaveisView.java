package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.StartFormData;
import br.com.infox.epp.processo.form.StartFormDataImpl;
import br.com.infox.ibpm.util.JbpmUtil;

@Named
@ViewScoped
public class IniciarProcessoVariaveisView implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Inject
//    private JsfUtil jsfUtil;
    
    private Processo processo;
    private ProcessDefinition processDefinition;
    private StartFormData formData;
    
    @PostConstruct
    private void init() {
//        processo = jsfUtil.getFlashParam("processo", Processo.class);
//        processDefinition = jsfUtil.getFlashParam("processDefinition", ProcessDefinition.class);
        processo = EntityManagerProducer.getEntityManager().find(Processo.class, 414);
        processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processo.getNaturezaCategoriaFluxo().getFluxo().getFluxo());
        formData = new StartFormDataImpl(processo, processDefinition);
    }
    
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void gravar() {
        formData.toString();
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
