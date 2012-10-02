/*
 * IBPM - Ferramenta de produtividade Java
 * Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 * Free Software Foundation; vers�o 2 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU GPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/  
 */
package br.com.infox.ibpm.jbpm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.jbpm.handler.VariableHandler;
import br.com.itx.component.Form;
import br.com.itx.component.FormField;
import br.com.itx.component.Template;
import br.com.itx.util.EntityUtil;


/**
 * Gera um formulario a partir do controller da tarefa atual (taskInstance)
 * Para a geracao correta o atributo mapped-name deve seguir o padrao:
 * 
 * 		tipo:nome_da_variavel
 * 
 * Onde:
 * 		- tipo � o nome do componente de formulario para o campo
 * 		- nome_da_variavel � como sera armazenada no contexto.
 * 				Serve tamb�m para gerar o label (Nome da variavel)
 * 
 * Esse formulario contem apenas campos somente leitura (access=read),
 * para os outros campos � usada a classe TaskInstanceForm
 * 
 * @author luizruiz
 *
 */

@Name("taskInstanceView")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class TaskInstanceView implements Serializable{

	private static final long serialVersionUID = 1L;

	private Form form;

	private TaskInstance taskInstance;
	

	@Unwrap
	public Form getTaskForm() {
		getTaskInstance();
		if (form != null || taskInstance == null) {
			return form;
		}
		form = new Form();
		form.setHome("taskInstanceHome");
		Template buttons = new Template();
		buttons.setId("empty");
		form.setButtons(buttons);
		form.setFormId("taskInstanceView");

		TaskController taskController = taskInstance.getTask().getTaskController();
		if (taskController != null) {
			List<VariableAccess> list = taskController.getVariableAccesses();
			
			for (VariableAccess var : list) {
				if (var.isReadable() && !var.isWritable()) {
					String[] tokens = var.getMappedName().split(":");
					String type = tokens[0];
					String name = tokens[1];
					FormField ff = new FormField();
					ff.setFormId(form.getFormId());
					ff.setId(var.getVariableName());
					ff.setRequired(var.isRequired() + "");
					ff.setLabel(VariableHandler.getLabel(name));
					Object value = taskInstance.getVariable(var.getVariableName());
					Map<String, Object> properties = new HashMap<String, Object>();
					if(type.startsWith("textEdit")) {
						ff.setType("textEditComboReadonly");
						if (value != null) {
							ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, (Integer) value);
							if(processoDocumento != null){
								properties.put("modeloDocumentoRO", processoDocumento.getProcessoDocumentoBin().getModeloDocumento());
								properties.put("tipoProcessoDocumentoRO", processoDocumento.getTipoProcessoDocumento());
							}
						}
					} else {
						ff.setType(type);
					}
					properties.put("readonly", !var.isWritable());
					if (value == null && !var.isWritable() && "textEdit".equals(type)) {
						properties.put("rendered", "false");
					}
					ff.setProperties(properties);
					form.getFields().add(ff);
				}
			}
		}
		return form ;
	}


	private void getTaskInstance() {
		TaskInstance newInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (newInstance == null || !newInstance.equals(taskInstance)) {
			form = null;
		}
		taskInstance = newInstance;
	}
}