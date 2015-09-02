package br.com.infox.epp.documento.type;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;
import br.com.infox.seam.util.ComponentUtil;

public class JbpmExpressionResolver implements ExpressionResolver {
	private Map<String, Pair<String, VariableType>> variableTypeMap;
	private Integer idProcesso;
	
	public JbpmExpressionResolver(Map<String, Pair<String, VariableType>> variableTypeMap, Integer idProcesso) {
		if (variableTypeMap == null) {
			throw new NullPointerException("O mapa de variáveis não pode ser nulo");
		}
		if (idProcesso == null) {
			throw new NullPointerException("O id do processo não pode ser nulo");
		}
		this.variableTypeMap = variableTypeMap;
		this.idProcesso = idProcesso;
	}
	
	@Override
	public Expression resolve(Expression expression) {
		String realVariableName = expression.getExpression().substring(2, expression.getExpression().length() - 1);
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		EntityManager entityManager = ComponentUtil.getComponent("entityManager");
		Object value = null;
		Processo processo = processoManager.find(idProcesso);
		do {
			ProcessInstance processInstance = entityManager.find(ProcessInstance.class, processo.getIdJbpm());
	        value = processInstance.getContextInstance().getVariable(realVariableName);
	        Pair<String, VariableType> variableInfo = variableTypeMap.get(realVariableName);
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

	private void resolveAsJavaType(Expression expression, Object value) {
		if (value instanceof Date) {
			expression.setValue(new SimpleDateFormat("dd/MM/yyyy").format(value));
		} else if (value instanceof Boolean) {
			expression.setValue((Boolean) value ? "Sim" : "Não");
		} else {
			expression.setValue(value.toString());
		}
		expression.setResolved(true);
	}

	private void resolveAsVariableType(Expression expression, Object value,  Pair<String, VariableType> variableInfo) {
		expression.setResolved(true);
		switch (variableInfo.getRight()) {
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
		    DominioVariavelTarefa dominio = dominioVariavelTarefaManager.find(Integer.valueOf(variableInfo.getLeft().split(":")[2]));
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
		    break;
		}
	}
}
