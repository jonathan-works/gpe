package br.com.infox.epp.processo.iniciar;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.Holder;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.StartFormData;
import br.com.infox.epp.processo.form.StartFormDataImpl;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jsf.util.JsfProducer.FlashParam;
import br.com.infox.jsf.util.JsfProducer.ParamValue;

@Named
@ViewScoped
public class IniciarProcessoVariaveisView extends AbstractIniciarProcesso {

    private static final long serialVersionUID = 1L;

    private Holder<Processo> processo;
    private ProcessDefinition processDefinition;
    private StartFormData formData;
    
    @Inject @FlashParam 
    private ParamValue<Processo> paramProcesso;
    
    @PostConstruct
    private void init() {
        if (paramProcesso.isNull()) {
            jsfUtil.redirect("/Processo/listView.seam");
        } else {
            processo = new Holder<>(paramProcesso.getValue());
            processDefinition = JbpmUtil.instance().findLatestProcessDefinition(getProcesso().getNaturezaCategoriaFluxo().getFluxo().getFluxo());
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
        if ( !formData.isInvalid() ) {
            Map<String, Object> variables = formData.getVariables();
            iniciarProcesso(getProcesso(), variables);
            return "/Painel/list.seam";
        }
        return null;
    }
    
    public StartFormData getFormData() {
        return formData;
    }

    public Processo getProcesso() {
        return processo.value;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }
    
}
