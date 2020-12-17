package br.com.infox.ibpm.variable.components;

import java.util.Optional;

import javax.xml.ws.Holder;

import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.jsf.util.JsfUtil;

public abstract class AbstractTaskPageController implements TaskpageController {

    private Holder<TaskInstance> taskInstanceHolder;
    private Holder<Processo> processoHolder;
    private TaskpageDefinition taskpageDefinition;

    @Override
    public void initialize(Holder<TaskInstance> taskInstanceHolder, Holder<Processo> processo, TaskpageDefinition taskpageDefinition) {
        this.taskInstanceHolder = taskInstanceHolder;
        this.processoHolder = processo;
        this.taskpageDefinition = taskpageDefinition;
        initialize();
    }

    protected void initialize() {
    }

    @Override
    public void preFinalizarTarefa(Transition transition, TaskFormData formData) {
    }

    @Override
    public void finalizarTarefa(Transition transition, TaskFormData formData) {

    }

    private TaskInstance retrieveTaskInstanceFromRequest() {
        String requestParameter = Optional.ofNullable(JsfUtil.instance().getRequestParameter("idTaskInstance")).orElse("").trim();
        Long idTaskInstance;
        if (requestParameter.matches("^\\d+$")) {
            idTaskInstance = Long.parseLong(requestParameter, 10);
        } else {
            idTaskInstance = ProcessoEpaHome.instance().getIdTaskInstance();
        }
        return EntityManagerProducer.instance().getEntityManagerNotManaged().find(TaskInstance.class, idTaskInstance);
    }

    protected TaskInstance getTaskInstance() {
        return taskInstanceHolder == null ? retrieveTaskInstanceFromRequest() : taskInstanceHolder.value;
    }

    protected Processo getProcesso() {
        return processoHolder == null ? null : processoHolder.value;
    }

    public TaskpageDefinition getTaskpageDefinition() {
        return taskpageDefinition;
    }

    public Object getVariable(String variableName){
        if( !StringUtil.isEmpty(variableName) ) {
            return getTaskInstance().getContextInstance().getVariable(variableName);
        }
        return null;
    }

    public <T> T getVariable(String variableName, Class<T> returnType){
        Object variable = getTaskInstance().getContextInstance().getVariable(variableName);
        return returnType.cast(variable);
    }

    public void setVariable(String variableName, Object value){
        if( !StringUtil.isEmpty(variableName) ) {
            getTaskInstance().getContextInstance().setVariable(variableName,value);
        }
    }

    public void setVariable(String variableName, Object value, Token token){
        if( !StringUtil.isEmpty(variableName) ) {
            getTaskInstance().getContextInstance().setVariable(variableName, value, token);
        }
    }

    @Override
    public boolean canCompleteTask() {
        return true;
    }

    @Override
    public String getIdFormButtons() {
        return "idFormButtons";
    }

    @Override
    public String onClick() {
        return "";
    }

}
