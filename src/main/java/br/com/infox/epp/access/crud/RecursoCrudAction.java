package br.com.infox.epp.access.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;

@Name(RecursoCrudAction.NAME)
public class RecursoCrudAction extends AbstractCrudAction<Papel> {
    
    public static final String NAME = "recursoCrudAction";

}
