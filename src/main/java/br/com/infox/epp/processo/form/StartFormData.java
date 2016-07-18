package br.com.infox.epp.processo.form;

import org.jbpm.graph.def.ProcessDefinition;

public interface StartFormData extends FormData {

    ProcessDefinition getProcessDefinition();
    
}
