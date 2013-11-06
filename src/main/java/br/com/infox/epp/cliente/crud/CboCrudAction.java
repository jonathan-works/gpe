package br.com.infox.epp.cliente.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.cliente.entity.Cbo;

@Name(CboCrudAction.NAME)
@Scope(ScopeType.PAGE)
public class CboCrudAction extends AbstractCrudAction<Cbo> {
    
    public static final String NAME = "cboCrudAction";

}
