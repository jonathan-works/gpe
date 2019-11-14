package br.com.infox.ibpm.task.home;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.ibpm.variable.entity.VariableInfo;
import br.com.infox.ibpm.variable.service.VariableTypeResolverService;

@Name(VariableTypeResolver.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@ContextDependency
public class VariableTypeResolver implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "variableTypeResolver";
    
    @In(required = false)
    private ProcessInstance processInstance;
    
    @Inject
    private VariableTypeResolverService variableTypeResolverService;
    
    private Map<String, VariableInfo> variableInfoMap;
    
    @PostConstruct
    public void init() {
        variableInfoMap = new HashMap<>();
        if (processInstance != null) {
            buildVariableInfoMap();
        }
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        buildVariableInfoMap();
    }
    
    public Map<String, VariableInfo> getVariableInfoMap() {
        return variableInfoMap;
    }
    
    private void buildVariableInfoMap() {
    	if (processInstance != null) {
    		variableInfoMap = Collections.unmodifiableMap(variableTypeResolverService.buildVariableInfoMap(processInstance.getProcessDefinition().getId()));
    	} else {
    		variableInfoMap = new HashMap<>();
    	}
    }
}
