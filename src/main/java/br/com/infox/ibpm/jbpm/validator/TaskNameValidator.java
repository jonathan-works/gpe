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
package br.com.infox.ibpm.jbpm.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.Component;
import org.jbpm.graph.def.Node;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.ibpm.jbpm.fitter.NodeFitter;
import br.com.infox.ibpm.jbpm.fitter.TaskFitter;

@FacesValidator(value = TaskNameValidator.VALIDATOR_ID)
public class TaskNameValidator implements Validator {

	public static final String VALIDATOR_ID = "taskNameValidator";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		NodeFitter nodeFitter = (NodeFitter) Component.getInstance(NodeFitter.NAME);
		TaskFitter taskFitter = (TaskFitter) Component.getInstance(TaskFitter.NAME);
		
		for (Node node : nodeFitter.getNodes()) {
			Task task = taskFitter.getCurrentTask().getTask();
			if (!node.equals(task.getTaskNode()) && node.getName().equals(value)) {
				throw new ValidatorException(new FacesMessage("Já existe um nó com o nome informado"));
			}
		}
	}
}