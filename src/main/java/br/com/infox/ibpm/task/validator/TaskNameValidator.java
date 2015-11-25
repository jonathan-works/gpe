package br.com.infox.ibpm.task.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jbpm.graph.def.Node;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;

@FacesValidator(value = TaskNameValidator.VALIDATOR_ID)
public class TaskNameValidator implements Validator {

    public static final String VALIDATOR_ID = "taskNameValidator";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
        NodeFitter nodeFitter = BeanManager.INSTANCE.getReference(NodeFitter.class);
        TaskFitter taskFitter = BeanManager.INSTANCE.getReference(TaskFitter.class);

        if (ProcessBuilder.instance().existemProcessosAssociadosAoFluxo()) {
            throw new ValidatorException(
                    new FacesMessage("Esta ação não pode ser executada enquanto o nó possuir atividade em fluxo instanciado"));
        }

        for (Node node : nodeFitter.getNodes()) {
            Task task = taskFitter.getCurrentTask().getTask();
            if (!node.equals(task.getTaskNode()) && node.getName().equals(value)) {
                throw new ValidatorException(new FacesMessage("Já existe um nó com o nome informado"));
            }
        }
    }
}
