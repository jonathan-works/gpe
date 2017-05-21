package br.com.infox.ibpm.variable.components;

import javax.xml.ws.Holder;

import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.TaskFormData;

public interface TaskpageController {
    
    void initialize(Holder<TaskInstance> taskInstanceHolder, Holder<Processo> processo, TaskpageDefinition taskpageDefinition);
    
    void preFinalizarTarefa(Transition transition, TaskFormData formData);
    
    void finalizarTarefa(Transition transition, TaskFormData formData);
    
    boolean canCompleteTask();
    
    String getIdFormButtons();
    
}
