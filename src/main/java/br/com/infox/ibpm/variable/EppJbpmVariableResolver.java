package br.com.infox.ibpm.variable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.el.impl.JbpmVariableResolver;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.system.custom.variables.CustomVariable;
import br.com.infox.epp.system.custom.variables.CustomVariableSearch;
import br.com.infox.epp.system.custom.variables.TipoCustomVariableEnum;

public class EppJbpmVariableResolver extends JbpmVariableResolver {
    
    public EppJbpmVariableResolver() {
    }
    
    @Override
    public Object resolveVariable(String name) throws ELException {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        Object object = super.resolveVariable(name);
        if (object != null) return object;
        
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable("processo");
        Processo processo = BeanManager.INSTANCE.getReference(ProcessoManager.class).find(idProcesso);
        object = resolveMetadadoProcesso(name, processo);
        
        while (processo.getProcessoPai() != null && object == null) {
            processo = processo.getProcessoPai();
            object = resolveVariable(name, processo);
            if (object == null) {
                object = resolveMetadadoProcesso(name, processo);
            }
        }
        
        if (object == null) {
        	object = resolveCustomVariable(name);
        }
        
        return object;
    }
    
    public Object resolveVariable(String name, Processo processo) {
        String jpql = "select pi from org.jbpm.graph.exe.ProcessInstance pi where pi.id = :idJbpm";
        TypedQuery<ProcessInstance> typedQuery = BeanManager.INSTANCE.getReference(EntityManager.class).createQuery(jpql, ProcessInstance.class);
        ProcessInstance processInstance = typedQuery.setParameter("idJbpm", processo.getIdJbpm()).getSingleResult();
        return processInstance.getContextInstance().getVariable(name);
    }
    
    public Object resolveMetadadoProcesso(String name, Processo processo) {
        List<MetadadoProcesso> metadados = BeanManager.INSTANCE.getReference(MetadadoProcessoManager.class).getMetadadoProcessoByType(processo, name);
        return getMetadadoValue(metadados);
    }
    
    private Object getMetadadoValue(List<MetadadoProcesso> metadados) {
        if (metadados == null || metadados.isEmpty()) return null;
        if (metadados.size() == 1) {
            return metadados.get(0).getValue();
        } else {
            List<Object> resultList = new ArrayList<>(metadados.size());
            for (MetadadoProcesso metadado : metadados) {
                resultList.add(metadado.getValue());
            }
            return resultList;
        }
    }
    
    private Object resolveCustomVariable(String name) {
    	ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
    	CustomVariableSearch customVariableSearch = BeanManager.INSTANCE.getReference(CustomVariableSearch.class);
    	Object result = null;
		CustomVariable customVariable = customVariableSearch.getCustomVariable(name);
		if (customVariable != null) {
			if (customVariable.getTipo() == TipoCustomVariableEnum.EL) {
				result = JbpmExpressionEvaluator.evaluate(customVariable.getValor(), executionContext);
			} else {
				result = customVariable.getTypedValue();
			}
		}
		return result;
    }
}
