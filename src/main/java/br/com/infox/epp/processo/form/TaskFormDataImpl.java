package br.com.infox.epp.processo.form;

import java.util.Map;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.processo.entity.Processo;

public class TaskFormDataImpl extends AbstractFormData implements TaskFormData {
    
    protected ExpressionResolverChain expressionResolver;
    protected TaskInstance taskInstance;
    
    public TaskFormDataImpl(Processo processo, TaskInstance taskInstance) {
        super("taskForm", processo);
        this.taskInstance = taskInstance;
        expressionResolver = ExpressionResolverChainBuilder.defaultExpressionResolverChain(getProcesso().getIdProcesso(), getTaskInstance());
    }

    @Override
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    @Override
    public Object getVariable(String name) {
        return taskInstance.getVariable(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        taskInstance.setVariable(name, value);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, Object> getVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExpressionResolverChain getExpressionResolver() {
        return expressionResolver;
    }

}
