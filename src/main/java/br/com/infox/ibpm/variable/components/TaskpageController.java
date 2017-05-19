package br.com.infox.ibpm.variable.components;

import javax.xml.ws.Holder;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;

interface TaskpageController {
    
    public void initialize(Holder<TaskInstance> taskInstanceHolder, Holder<Processo> processo);

}
