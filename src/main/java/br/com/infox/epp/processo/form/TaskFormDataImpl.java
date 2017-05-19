package br.com.infox.epp.processo.form;

import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.executarTarefa.ExecutarTarefaService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.ibpm.process.definition.variable.VariableType;

public class TaskFormDataImpl extends AbstractFormData implements TaskFormData {
    
    protected static final String MAPPED_NAME_PROPERTY = "mapped-name";
    
    protected ExpressionResolverChain expressionResolver;
    protected Holder<TaskInstance> taskInstanceHolder;
    
    public TaskFormDataImpl(Holder<Processo> processo, Holder<TaskInstance> taskInstanceHolder) {
        super("taskForm", processo);
        this.taskInstanceHolder = taskInstanceHolder;
        expressionResolver = ExpressionResolverChainBuilder.defaultExpressionResolverChain(getProcesso().getIdProcesso(), getTaskInstance());
        
        List<VariableAccess> variableAccesses = getTaskInstance().getTask().getTaskController().getVariableAccesses();        
        createFormFields(variableAccesses);
    }

    @Override
    protected void createFormFields(List<VariableAccess> variableAccesses) {
    	VariableAccess variableTaskPage = getTaskPage(variableAccesses);
		if (variableTaskPage != null) {
			createFormField(variableTaskPage);
		} else {
			for (VariableAccess variableAccess : variableAccesses) {
				String type = variableAccess.getType();
				if (!VariableType.PARAMETER.name().equals(type)) {
					createFormField(variableAccess);
				}
			}
		}
    }
    
    @Override
    public TaskInstance getTaskInstance() {
        return taskInstanceHolder.value;
    }
    
    @Override
    public Object getVariable(String name) {
        return getTaskInstance().getVariable(name);
    }

    @Override
    public void setSingleVariable(FormField formField, Object value) {
        String mappedName = formField.getProperty(MAPPED_NAME_PROPERTY, String.class);
        getExecutarTarefaService().gravarUpload(mappedName, (TypedValue) value, this);
    }
    
    @Override
    public void setVariable(String name, Object value) {
    	getTaskInstance().setVariableLocally(name, value);
    }
    
    @Override
    protected Map<String, Object> createProperties(VariableAccess variableAccess) {
        Map<String, Object> properties = super.createProperties(variableAccess);
        properties.put(MAPPED_NAME_PROPERTY, variableAccess.getMappedName());
        return properties;
    }

    @Override
    public void update() {
    	for (FormField formField : getFormFields()) {
            if (formField.getType().isPersistable() && formField.getValue() != null) {
                String mappedName = formField.getProperty(MAPPED_NAME_PROPERTY, String.class);
            	FormType type = formField.getType();
            	type.performUpdate(formField, this);
                setVariable(mappedName, type.getValueType().convertToModelValue(formField.getValue()));
            }
        }
    }

    @Override
    public Map<String, Object> getVariables() {
        return getTaskInstance().getVariables();
    }

    @Override
    public ExpressionResolverChain getExpressionResolver() {
        return expressionResolver;
    }

    @Override
    public Node getNode() {
        return getTaskInstance().getTask().getTaskNode();
    }
    
    private ExecutarTarefaService getExecutarTarefaService(){
        return BeanManager.INSTANCE.getReference(ExecutarTarefaService.class);
    }

}
