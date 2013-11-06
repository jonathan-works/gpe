package br.com.infox.epp.cliente.crud;

import org.jboss.seam.annotations.Name;
import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.cliente.entity.Cbo;

@Name(CboCrudAction.NAME)
public class CboCrudAction extends AbstractCrudAction<Cbo> {
    
    public static final String NAME = "cboCrudAction";

}
