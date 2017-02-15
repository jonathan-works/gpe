package br.com.infox.epp.processo.form;

import org.jbpm.taskmgmt.exe.TaskInstance;

public interface TaskFormData extends FormData {

    TaskInstance getTaskInstance();
    
    void setTaskInstance(TaskInstance taskInstance);

}
