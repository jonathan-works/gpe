package br.com.infox.ibpm.node.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jbpm.graph.def.Node;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;

@FacesValidator(value = NodeNameValidator.VALIDATOR_ID)
public class NodeNameValidator implements Validator {

    public static final String VALIDATOR_ID = "nodeNameValidator";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
        NodeFitter nodeFitter = BeanManager.INSTANCE.getReference(NodeFitter.class);
        for (Node node : nodeFitter.getNodes()) {
            if (node.getName().equals(value)) {
                throw new ValidatorException(new FacesMessage("Já existe um nó com o nome informado"));
            }
        }
    }
}
