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

package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.ibpm.context.InfoxManagedJbpmContext;
import br.com.itx.util.ComponentUtil;


@Name(VariableHandler.NAME)
public class VariableHandler implements Serializable {
	private static final long serialVersionUID = -6777955765635127593L;
	
	public static final String NAME = "variableHandler";

	private transient LogProvider log = Logging.getLogProvider(VariableHandler.class);
	
	public List<Variavel> getVariables(long taskId) {
		return getVariables(taskId, false);
	}

	public List<Variavel> getTaskVariables(long taskId) {
		return getVariables(taskId, true);
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	private List<Variavel> getVariables(long taskId, boolean readOnly) {
		List<Variavel> ret = new ArrayList<Variavel>();
		TaskInstance taskInstance = InfoxManagedJbpmContext.instance()
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

}