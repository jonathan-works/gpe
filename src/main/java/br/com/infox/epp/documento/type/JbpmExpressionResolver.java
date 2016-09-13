package br.com.infox.epp.documento.type;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.entity.VariableInfo;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.ibpm.variable.service.VariableTypeResolverService;
import br.com.infox.seam.util.ComponentUtil;

public class JbpmExpressionResolver implements ExpressionResolver {
	// <Id Process Definition, <Variable Name, Variable Info>>
	private Map<Long, Map<String, VariableInfo>> variableInfoMap = new HashMap<>();
	private Integer idProcesso;
	private ProcessInstance  processInstance;
	
	public JbpmExpressionResolver(Integer idProcesso) {
		if (idProcesso == null) {
			throw new NullPointerException("O id do processo não pode ser nulo");
		}
		this.idProcesso = idProcesso;
	}
	
	public JbpmExpressionResolver(ProcessInstance processInstance) {
		if (processInstance == null) {
			throw new NullPointerException("O id do process instance não pode ser nulo");
		}
		this.processInstance = processInstance;
	}
	
	@Override
	public Expression resolve(Expression expression) {
		String realVariableName = expression.getExpression().substring(2, expression.getExpression().length() - 1);
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		Object value = null;
		
		if(idProcesso == null && processInstance != null)
			idProcesso = (Integer) processInstance.getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
		
		Processo  processo = entityManager.find(Processo.class, idProcesso);
		if(processInstance == null)
			processInstance = entityManager.find(ProcessInstance.class, processo.getIdJbpm());
		ProcessInstance  procInst = processInstance;
		do {
	        value = resolveValue(procInst, realVariableName, expression);
	        if(value == null){
	        	//procura na hierarquia de processos acessórios 
	        	processo = processo.getProcessoPai();
	        	if(processo != null)
	        		procInst = entityManager.find(ProcessInstance.class, processo.getIdJbpm());
	        }
		} while (value == null && processo != null);
		
		
		
        return expression;
	}
	
	private Object resolveValue(ProcessInstance processInstance, String realVariableName,Expression expression){
		Object value;
		do {
			VariableInfo variableInfo = getVariableInfo(realVariableName, processInstance.getProcessDefinition().getId());
			value = processInstance.getContextInstance().getVariable(realVariableName);
			if (variableInfo == null && value != null) {
				resolveAsJavaType(expression, value);
			} else if (variableInfo != null && value != null) {
				resolveAsVariableType(expression, value, variableInfo);
			} else {
				//procura na hierarquia de sub-processos
				Token superProcessToken = processInstance.getSuperProcessToken();
				processInstance = superProcessToken != null ? superProcessToken.getProcessInstance() : null;
			}
		} while (value == null && processInstance != null);
		
		return value;
	}

	private VariableInfo getVariableInfo(String variableName, Long processDefinitionId) {
		if (!variableInfoMap.containsKey(processDefinitionId)) {
			VariableTypeResolverService variableTypeResolverService = BeanManager.INSTANCE.getReference(VariableTypeResolverService.class);
			variableInfoMap.put(processDefinitionId, variableTypeResolverService.buildVariableInfoMap(processDefinitionId));
		}
		return variableInfoMap.get(processDefinitionId).get(variableName);
	}
	
	private void resolveAsJavaType(Expression expression, Object value) {
		if (value instanceof Date) {
			expression.setValue(new SimpleDateFormat("dd/MM/yyyy").format(value));
		} else if (value instanceof Boolean) {
			expression.setValue((Boolean) value ? "Sim" : "Não");
		} else {
			expression.setValue(value.toString());
		}
		expression.setResolved(true);
		expression.setOriginalValue(value);
	}

	private void resolveAsVariableType(Expression expression, Object value, VariableInfo variableInfo) {
		expression.setResolved(true);
		expression.setOriginalValue(value);
		switch (variableInfo.getVariableType()) {
		case DATE:
		    expression.setValue(new SimpleDateFormat("dd/MM/yyyy").format(value));
		    break;

		case EDITOR:
			DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
		    expression.setValue(documentoManager.find(value).getDocumentoBin().getModeloDocumento());
		    break;
		    
		case MONETARY:
		    expression.setValue(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value));
		    break;
		    
		case TEXT:
		    expression.setValue(((String) value).replaceAll("[\n]+?|[\r\n]+?", "<br />"));
		    break;
		    
		case BOOLEAN:
		    expression.setValue(Boolean.valueOf((String) value) ? "Sim" : "Não");
		    break;
		    
		case ENUMERATION:
			DominioVariavelTarefaManager dominioVariavelTarefaManager = ComponentUtil.getComponent(DominioVariavelTarefaManager.NAME);
		    DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(Integer.valueOf(variableInfo.getMappedName().split(":")[2]));
		    String[] itens = dominio.getDominio().split(";");
		    for (String item : itens) {
		        String[] pair = item.split("=");
		        if (pair[0].equals(value))  {
		            expression.setValue(pair[1]);
		        }
		    }
		    break;
		case FRAGMENT:
		    String string = value.toString();
            expression.setValue(string);
		    break;
		    
		case STRUCTURED_TEXT:
			expression.setValue(value.toString());
			break;
		default:
			expression.setResolved(false);
			expression.setOriginalValue(null);
		    break;
		}
	}
}
