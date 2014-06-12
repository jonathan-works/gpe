package br.com.infox.ibpm.task.home;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.process.definition.variable.VariableType;

@Name(VariableTypeResolver.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class VariableTypeResolver implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "variableTypeResolver";
    
    @In(required = false)
    private ProcessInstance processInstance;
    
    private Map<String, Pair<String, VariableType>> variableTypeMap;
    
    @PostConstruct
    public void init() {
        variableTypeMap = new HashMap<>();
        if (processInstance != null) {
            buildVariableTypeMap();
        }
    }
    
    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        buildVariableTypeMap();
    }
    
    public Map<String, Pair<String, VariableType>> getVariableTypeMap() {
        return Collections.unmodifiableMap(variableTypeMap);
    }
    
    private void buildVariableTypeMap() {
        variableTypeMap = new HashMap<>();
        Node start = processInstance.getProcessDefinition().getStartState();
        traverse(start);
    }

    @SuppressWarnings(UNCHECKED)
    private void traverse(Node node) {
        Node nodeWithoutProxy = (Node) HibernateUtil.removeProxy(node);
        if (nodeWithoutProxy instanceof TaskNode) {
            Set<Task> tasks = ((TaskNode) nodeWithoutProxy).getTasks();
            for (Task task : tasks) {
                if (task.getTaskController() != null) {
                    List<VariableAccess> variables = task.getTaskController().getVariableAccesses();
                    for (VariableAccess variable : variables) {
                        if (!variableTypeMap.containsKey(variable.getVariableName())) {
                            String mappedName = variable.getMappedName();
                            variableTypeMap.put(variable.getVariableName(), new ImmutablePair<>(mappedName, VariableType.valueOf(variable.getMappedName().split(":")[0])));
                        }
                    }
                }
            }
        }
        List<Transition> leavingTransitions = node.getLeavingTransitions();
        for (Transition transition : leavingTransitions) {
            traverse(transition.getTo());
        }
    }
}
