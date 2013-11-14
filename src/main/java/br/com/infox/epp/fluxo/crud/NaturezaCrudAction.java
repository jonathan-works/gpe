package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Natureza;

@Name(NaturezaCrudAction.NAME)
@Scope(ScopeType.PAGE)
public class NaturezaCrudAction extends AbstractCrudAction<Natureza> {
    
    public static final String NAME = "naturezaCrudAction";

}
