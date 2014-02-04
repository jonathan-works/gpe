package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.manager.VariavelManager;

@Name(VariavelCrudAction.NAME)
public class VariavelCrudAction extends AbstractCrudAction<Variavel, VariavelManager> {
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "variavelCrudAction";
	
}
