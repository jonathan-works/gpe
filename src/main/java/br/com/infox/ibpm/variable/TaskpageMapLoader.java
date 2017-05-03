package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.ibpm.variable.components.FrameDefinition;
import br.com.infox.ibpm.variable.components.TaskpageDefinition;
import br.com.infox.ibpm.variable.components.VariableDefinitionService;

@Named
@ViewScoped
public class TaskpageMapLoader implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private VariableDefinitionService variableDefinitionService;

    public TaskpageDefinition getTaskpage(String id) {
        return variableDefinitionService.getTaskPage(id);
    }
    
    public FrameDefinition getFrame(String id) {
    	return variableDefinitionService.getFrame(id);
    }

	public Collection<TaskpageDefinition> getTaskpages() {
		return variableDefinitionService.getTaskpages();
	}

	public Collection<FrameDefinition> getFrames() {
		return variableDefinitionService.getFrames();
	}
    
}
