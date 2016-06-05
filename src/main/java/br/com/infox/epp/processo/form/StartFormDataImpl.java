package br.com.infox.epp.processo.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.core.Expressions;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.VariavelInicioProcesso;
import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.epp.processo.service.VariavelInicioProcessoService;
import br.com.infox.ibpm.process.definition.variable.VariableType;

public class StartFormDataImpl extends AbstractFormData implements StartFormData {
    
    protected ProcessDefinition processDefinition;
    protected ExpressionResolverChain expressionResolver;
    
    public StartFormDataImpl(Processo processo, ProcessDefinition processDefinition) {
        super("startForm", processo);
        this.processDefinition = processDefinition;
        this.expressionResolver = new ExpressionResolverChain(new SeamExpressionResolver());
        createFormFields();
    }
    
    private void createFormFields() {
        List<VariableAccess> variableAccesses = getProcessDefinition().getTaskMgmtDefinition().getStartTask().getTaskController().getVariableAccesses();
        createFormFields(variableAccesses, getProcessDefinition());
        createParameters(variableAccesses);
    }

    private void createParameters(List<VariableAccess> variableAccesses) {
        for (VariableAccess variableAccess : variableAccesses) {
            String type = variableAccess.getMappedName().split(":")[0];
            if (VariableType.PARAMETER.name().equals(type)) {
                createParameter(variableAccess);
            }
        }
    }

    private void createParameter(VariableAccess variableAccess) {
        String name = variableAccess.getVariableName();
        String value = variableAccess.getValue();
        setVariable(name, new TypedValue(value, ValueType.PARAMETER));
    }

    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }
    
    public Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();
        for (FormField formField : getFormFields()) {
            FormType formType = formField.getType();
            if (formType != null && formType.isPersistable() && formField.getValue() != null) {
                variables.put(formField.getId(), formType.getValueType().convertToModelValue(formField.getValue()));
            }
        }
        return variables;
    }
    
    @Override
    public void update() {
        for (FormField formField : getFormFields()) {
            if (formField.getType().isPersistable() && formField.getValue() != null) {
                formField.getType().performUpdate(formField, this);
                setVariable(formField.getId(), new TypedValue(formField.getValue(), formField.getType().getValueType()));
            }
        }
    }

    @Override
    public Object getVariable(String name) {
        VariavelInicioProcesso variavel = getVariavelService().getVariavel(processo, name);
        if (variavel != null) {
            if (ValueType.PARAMETER.getName().equals(variavel.getType())) {
                String value = variavel.getValue();
                if (value.startsWith("#") || value.startsWith("$")) {
                    return Expressions.instance().createValueExpression(value).getValue();
                }
            } 
            return variavel.getTypedValue();
        } else {
            return null;
        }
    }

    @Override
    public void setVariable(String name, Object value) {
        getVariavelService().setVariavel(processo, name, (TypedValue) value);
    }
    
    @Override
    public ExpressionResolverChain getExpressionResolver() {
        return expressionResolver;
    }

    public VariavelInicioProcessoService getVariavelService() {
        return BeanManager.INSTANCE.getReference(VariavelInicioProcessoService.class);
    }

}
