/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
 */

package br.com.infox.ibpm.jbpm.handler;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.itx.util.ComponentUtil;


@Name(VariableHandler.NAME)
public class VariableHandler implements Serializable {
	private static final long serialVersionUID = -6777955765635127593L;
	
	public static final String NAME = "variableHandler";

	private transient LogProvider log = Logging.getLogProvider(VariableHandler.class);
	
//	private List<Variavel> variables;
//	private List<Variavel> taskVariables;
	
	public List<Variavel> getVariables(long taskId) {
		return getVariables(taskId, false);
	}

	public List<Variavel> getTaskVariables(long taskId) {
		return getVariables(taskId, true);
	}

	@SuppressWarnings("unchecked")
	private List<Variavel> getVariables(long taskId, boolean readOnly) {
		List<Variavel> ret = new ArrayList<Variavel>();
		TaskInstance taskInstance = ManagedJbpmContext.instance()
				.getTaskInstanceForUpdate(taskId);

		TaskController taskController = taskInstance.getTask()
				.getTaskController();
		if (taskController != null) {
			List<VariableAccess> list = taskController.getVariableAccesses();
			for (VariableAccess var : list) {
				if (readOnly && !var.isWritable()) {
					continue;
				}
				String type = var.getMappedName().split(":")[0];
				try {
					String name = var.getMappedName().split(":")[1];
					Object value = taskInstance.getVariable(var.getMappedName());
					if (value != null && !"".equals(value)) {
						ret.add(new Variavel(getLabel(name), value, type));
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					log.error("Varivel com Valor inválido: " + Strings.toString(var));
				}
			}
		}
		return ret;
	}
	
	
	public static String getLabel(String name) {
		Map<String, String> map = ComponentUtil.getComponent("jbpmMessages");
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			if (name.length() > 1) {
				String label = name.substring(0, 1).toUpperCase() + name.substring(1);
				return label.replaceAll("_", " ");
			} else {
				return name;
			}
		}
	}

	public static VariableHandler instance() {
		return (VariableHandler) Component.getInstance(NAME);
	}

	public class Variavel implements Serializable {
		
		private static final long serialVersionUID = 4536717298608507132L;
		private final String type;
		private final Object value;
		private final String label;

		public Variavel(String nome, Object valor, String tipo) {
			this.label = nome;
			this.value = valor;
			this.type = tipo;
		}

		public String getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

		//TODO ver com Ruiz se dá para usar o componente (type) pra mostar a variável... :P
		public String getValuePrint() {
			if (value instanceof Boolean) {
				Boolean var = (Boolean) value;
				if (var) {
					return "Sim";
				} else {
					return "Não";
				} 
			} 
			if (value instanceof Date) {
				return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(value);
			}
			return value.toString();
		}		
		
		public String getLabel() {
			return label;
		}
		
		public String toString() {
			return label + ": " + value;
		}
		
	}

}