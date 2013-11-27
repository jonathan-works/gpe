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
package br.com.infox.ibpm.jbpm.task.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.handler.VariableHandler;
import br.com.infox.ibpm.jbpm.task.home.TaskInstanceHome;
import br.com.itx.component.Form;
import br.com.itx.component.FormField;
import br.com.itx.component.Template;


/**
 * Gera um formulario a partir do controller da tarefa atual (taskInstance)
 * Para a geracao correta o atributo mapped-name deve seguir o padrao:
 * 
 * 		tipo:nome_da_variavel
 * 
 * Onde:
 * 		- tipo é o nome do componente de formulario para o campo
 * 		- nome_da_variavel é como sera armazenada no contexto.
 * 				Serve também para gerar o label (Nome da variavel)
 * 
 * Esse formulario contem apenas campos que possam ser escritos (access=write),
 * para os outros campos é usada a classe TaskInstanceView
 * 
 * @author luizruiz
 *
 */

@Name(TaskInstanceForm.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class TaskInstanceForm implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskInstaceForm";
	public static final String TASK_BUTTONS = "taskButtons";
	
	private Form form;

	private TaskInstance taskInstance;

	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Unwrap
	public Form getTaskForm() {
		getTaskInstance();
		if (form != null || taskInstance == null) {
			return form;
		}
		TaskController taskController = taskInstance.getTask().getTaskController();
		Template buttons = new Template();
		List<VariableAccess> list = null;
		if (taskController != null) {
			list = taskController.getVariableAccesses();
			for (VariableAccess var : list) {
				if (var.isReadable() && var.isWritable()) {
					String[] tokens = var.getMappedName().split(":");
					String type = tokens[0];
					String name = tokens[1];
					if ("form".equals(type)) { 
						String formName = name + "Form";
						form = (Form) Component.getInstance(formName);
						if (form != null) {
							buttons.setId(TASK_BUTTONS);
							form.setButtons(buttons);
							form.setHome(name + "Home");
						} else {
							FacesMessages.instance().add(StatusMessage.Severity.INFO,
								"O form '" + formName + "' não foi encontrado.");
						}
						return form;
					}
				}
			}
		}
		if(form == null) {
			form = new Form();
			form.setHome(TaskInstanceHome.NAME);
			form.setFormId("taskInstance");
			buttons.setId(TASK_BUTTONS);
			form.setButtons(buttons);
			addVariablesToForm(list);
		}
		return form ;
	}

	/**
	 * Adiciona as variaveis da list informada ao form que está sendo criado.
	 * @param list - Lista das variavéis que desejam ser adicionadas ao form.
	 */
	private void addVariablesToForm(List<VariableAccess> list) {
		if(list != null) {
			for (VariableAccess var : list) {
				if (var.isReadable() && var.isWritable()) {
					String[] tokens = var.getMappedName().split(":");
					String type = tokens[0];
					String name = tokens[1];
					Object variable = JbpmUtil.getProcessVariable(name + "Modelo");
					if (variable != null) {
						FormField ff = new FormField();
						ff.setFormId(form.getFormId());
						ff.setId(name + "Modelo");
						ff.setLabel("Modelo");
						ff.setType("comboModelos");	
						ff.setProperties(getInNewLineMap());
						form.getFields().add(ff);
					}
					FormField ff = new FormField();
					ff.setFormId(form.getFormId());
					ff.setId(var.getVariableName() + "-" + taskInstance.getId());
					ff.setRequired(var.isRequired() + "");
					ff.setLabel(VariableHandler.getLabel(name));
					ff.setType(type);
					form.getFields().add(ff);
					if ("page".equals(type) || "frame".equals(type)) { 
						String url = name.replaceAll("_", "/");
						url = "/" + url + ("page".equals(type) ? ".seam" : ".xhtml");
						String urlParamName = "page".equals(type) ? "url" : "urlFrame";
						Map<String, Object> props = new HashMap<String, Object>();
						props.put(urlParamName, url);
						ff.setProperties(props);
					}
				}
			}
		}
	}
	
	private void getTaskInstance() {
		TaskInstance newInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (newInstance == null || !newInstance.equals(taskInstance)) {
			form = null;
		}
		taskInstance = newInstance;
	}

	public Map<String, Object> getInNewLineMap() {
		Map<String, Object> mapProperties = new HashMap<String, Object>();
		mapProperties.put("inNewLine", "true");
		return mapProperties;
	}
	
}