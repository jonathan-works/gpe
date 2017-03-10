package br.com.infox.epp.ajuda.manager;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.ibpm.variable.components.ComponentDefinition;
import br.com.infox.ibpm.variable.components.Parameterized;

@ViewScoped
@Named
public class ComponentAjudaController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public boolean hasParameter(ComponentDefinition component) {
		if(component == null) {
			return false;
		}
		return component instanceof Parameterized;
	}

}
