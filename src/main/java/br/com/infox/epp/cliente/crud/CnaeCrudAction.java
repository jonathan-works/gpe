package br.com.infox.epp.cliente.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.cliente.entity.Cnae;

@Name(CnaeCrudAction.NAME)
public class CnaeCrudAction extends AbstractCrudAction<Cnae> {
    
    public static final String NAME = "cnaeCrudAction";

}
