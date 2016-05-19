package br.com.infox.epp.documento.type;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.entity.VariableInfo;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.ibpm.variable.service.VariableTypeResolverService;
import br.com.infox.seam.util.ComponentUtil;

public class JbpmExpressionResolver implements ExpressionResolver {
	// <Id Process Definition, <Variable Name, Variable Info>>
	private Map<Long, Map<String, VariableInfo>> variableInfoMap = new HashMap<>();
	private Integer idProcesso;
	
	public JbpmExpressionResolver(Integer idProcesso) {
		if (idProcesso == null) {
			throw new NullPointerException("O id do processo não pode ser nulo");
		}
		this.idProcesso = idProcesso;
	}
	
	@Override
	public Expression resolve(Expression expression) {
		String realVariableName = expression.getExpression().substring(2, expression.getExpression().length() - 1);
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		Object value = null;
		Processo processo = processoManager.find(idProcesso);
		do {
			ProcessInstance processInstance = entityManager.find(ProcessInstance.class, processo.getIdJbpm());
	        value = processInstance.getContextInstance().getVariable(realVariableName);
	        VariableInfo variableInfo = getVariableInfo(realVariableName, processInstance.getProcessDefinition().getId());
	        if (variableInfo == null && value != null) {
	        	resolveAsJavaType(expression, value);
	        } else if (variableInfo != null && value != null) {
	        	resolveAsVariableType(expression, value, variableInfo);
	        } else {
	        	processo = processo.getProcessoPai();
	        }
		} while (value == null && processo != null);
        return expression;
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
		default:
			expression.setResolved(false);
			expression.setOriginalValue(null);
		    break;
		}
	}
}
